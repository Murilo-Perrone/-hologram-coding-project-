package com.hologramsciences;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoField;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.csv.CSVRecord;

import io.atlassian.fugue.Option;

public class CSVRestaurantService {
    private final List<Restaurant> restaurantList;

    /**
     * TODO: Implement Me
     *
     * From the CSVRecord which represents a single line from src/main/resources/rest_hours.csv
     * Write a parser to read the line and create an instance of the Restaurant class (Optionally, using the Option class)
     *
     * Example Line:
     *
     *  "Burger Bar","Mon,Tue,Wed,Thu,Sun|11:00-22:00;Fri,Sat|11:00-0:00"
     *
     *  '|'   separates the list of applicable days from the hours span
     *  ';'   separates groups of (list of applicable days, hours span)
     *
     *  So the above line would be parsed as:
     *
     *  Map<DayOfWeek, OpenHours> m = new HashMap<>();
     *  m.put(MONDAY,    new OpenHours(LocalTime.of(11, 0), LocalTime.of(22, 0)));
     *  m.put(TUESDAY,   new OpenHours(LocalTime.of(11, 0), LocalTime.of(22, 0)));
     *  m.put(WEDNESDAY, new OpenHours(LocalTime.of(11, 0), LocalTime.of(22, 0)));
     *  m.put(THURSDAY,  new OpenHours(LocalTime.of(11, 0), LocalTime.of(22, 0)));
     *  m.put(SUNDAY,    new OpenHours(LocalTime.of(11, 0), LocalTime.of(22, 0)));
     *
     *  m.put(FRIDAY,    new OpenHours(LocalTime.of(11, 0), LocalTime.of(0, 0)));
     *  m.put(SATURDAY,  new OpenHours(LocalTime.of(11, 0), LocalTime.of(0, 0)));
     *
     *  Option.some(new Restaurant("Burger Bar", m))
     *
     * This method returns Option.some(parsedRestaurant),
     *       IF the String name, and Map<DayOfWeek, OpenHours> openHours is found in the CSV,
     *         - assume if both columns are in the CSV then they are both parsable.
     *       AND if all values in openHours have !startTime.equals(endTime)
     *
     * This method returns Option.none() when any of the OpenHours for a given restaurant have the same startTime and endDate
     *
     *
     * NOTE, the getDayOfWeek method should be helpful, and the LocalTime should be parsable by LocalDate.parse
     *
     */
    public static Option<Restaurant> parse(final CSVRecord r) {
        if (r == null || r.size() != 2)
            return Option.none();
        
        String name = r.get(0);
        String openHours = r.get(1);
        Map<DayOfWeek, Restaurant.OpenHours> parsedMap = parseOpenHour(openHours);
        return parsedMap.isEmpty() ? Option.none() : Option.some(new Restaurant(name, parsedMap));
    }

    /**
     * TODO: Implement me, This is a useful helper method
     */
    public static Map<DayOfWeek, Restaurant.OpenHours> parseOpenHour(final String openhoursString) {
        Map<DayOfWeek, Restaurant.OpenHours> map = new HashMap<>();
        // For each openHours part
        for (String openHoursPart : openhoursString.split(";")) {
            String[] split = openHoursPart.split("\\|");
            String[] days  = split[0].split(",");
            String[] hours = split[1].split("-");
            
            // Aborting if any empty hour interval is found
            if (hours[0].equals(hours[1]))
                return Collections.emptyMap();
            
            // Parsing the open hours
            Restaurant.OpenHours openHours = new Restaurant.OpenHours(
                LocalTime.parse(hours[0]),
                LocalTime.parse(hours[1]));

            // For each day of the week
            for (String dayString : days) {
                Option<DayOfWeek> day = getDayOfWeek(dayString);
                if (day.isDefined())
                    map.put(day.get(), openHours);
            }
        }
        
        return map;
    }

    public CSVRestaurantService() throws IOException {
        this.restaurantList = ResourceLoader.parseOptionCSV("rest_hours.csv", CSVRestaurantService::parse);
    }

    public List<Restaurant> getAllRestaurants() {
        return restaurantList;
    }

    /**
     *
     *  TODO: Implement me
     *
     *  A restaurant is considered open when the OpenHours for the dayOfWeek has:
     *
     *  startTime < localTime   && localTime < endTime
     *
     *  If the open hours are 16:00-20:00  Then
     *
     *  15:59 open = false
     *  16:00 open = false
     *  16:01 open = true
     *  20:00 open = false
     *
     *
     *  If the startTime endTime spans midnight, then consider an endTime up until 6:00 to be part of same DayOfWeek as the startTime
     *
     *  SATURDAY, OpenHours are: 20:00-04:00    SUNDAY, OpenHours are: 10:00-14:00
     *
     *  (SATURDAY, 03:00) => open = false
     *  (SUNDAY, 03:00)   => open = true
     *  (SUNDAY, 05:00)   => open = false
     *
     */
    public List<Restaurant> getOpenRestaurants(final DayOfWeek dayOfWeek, final LocalTime localTime) {
        LocalDate sundayDate = LocalDate.parse("2004-04-04"); // This is a Sunday
        LocalTime spanLimit = LocalTime.parse("06:00"); // Restaurant "Tim's Nighttime Banana Stand" is an exception which spans to 06:00
        return restaurantList.stream().filter((Restaurant restaurant) -> {
            for (Map.Entry<DayOfWeek, Restaurant.OpenHours> entry : restaurant.getOpenHoursMap().entrySet()) {
                DayOfWeek openDayOfWeek = entry.getKey();
                Restaurant.OpenHours hours = entry.getValue();
                LocalTime startTime = hours.getStartTime();
                LocalTime endTime = hours.getEndTime();

                LocalDateTime startDateTime = sundayDate.plusDays(openDayOfWeek.getValue()).atTime(startTime); // Monday has the value of 1
                LocalDateTime localDateTime = sundayDate.plusDays(dayOfWeek.getValue()).atTime(localTime);

                // Note: This is a replacement for hours.spansMidnight(), which isn't appropriate for this check
                // (even though using hours.spansMidnight() also passed current test cases)
                boolean spansMidnight = endTime.isBefore(startTime) && (endTime == spanLimit || endTime.isBefore(spanLimit));
                LocalDateTime endDateTime = spansMidnight
                    ? sundayDate.plusDays(openDayOfWeek.getValue() + 1).atTime(endTime)
                    : sundayDate.plusDays(openDayOfWeek.getValue()).atTime(endTime);

                if (localDateTime.isAfter(startDateTime) && localDateTime.isBefore(endDateTime))
                    return true;
            }
            return false;
        }).collect(Collectors.toList());
    }

    public List<Restaurant> getOpenRestaurantsForLocalDateTime(final LocalDateTime localDateTime) {
        return getOpenRestaurants(localDateTime.getDayOfWeek(), localDateTime.toLocalTime());
    }

    public static Option<DayOfWeek> getDayOfWeek(final String s) {

        if (s.equals("Mon")) {
            return Option.some(DayOfWeek.MONDAY);
        } else if (s.equals("Tue")) {
            return Option.some(DayOfWeek.TUESDAY);
        } else if (s.equals("Wed")) {
            return Option.some(DayOfWeek.WEDNESDAY);
        } else if (s.equals("Thu")) {
            return Option.some(DayOfWeek.THURSDAY);
         } else if (s.equals("Fri")) {
            return Option.some(DayOfWeek.FRIDAY);
        } else if (s.equals("Sat")) {
            return Option.some(DayOfWeek.SATURDAY);
        } else if (s.equals("Sun")) {
            return Option.some(DayOfWeek.SUNDAY);
        } else {
            return Option.none();
        }
    }

    public static <S, T> Function<S, Stream<T>> toStreamFunc(final Function<S, Option<T>> function) {
        return s -> function.apply(s).fold(() -> Stream.empty(), t -> Stream.of(t));
    }

    /**
     * NOTE: Useful for generating the data.sql file in src/main/resources/
     */
    public static void main (final String [] args) throws IOException {
        final CSVRestaurantService csvRestaurantService = new CSVRestaurantService();

        csvRestaurantService.getAllRestaurants().forEach(restaurant -> {

            final String name = restaurant.getName().replaceAll("'", "''");

            System.out.println("INSERT INTO restaurants (name) values ('" + name  + "');");

            restaurant.getOpenHoursMap().entrySet().forEach(entry -> {
                final DayOfWeek dayOfWeek = entry.getKey();
                final LocalTime startTime = entry.getValue().getStartTime();
                final LocalTime endTime   = entry.getValue().getEndTime();

                System.out.println("INSERT INTO open_hours (restaurant_id, day_of_week, start_time_minute_of_day, end_time_minute_of_day) select id, '" + dayOfWeek.toString() + "', " + startTime.get(ChronoField.MINUTE_OF_DAY) + ", " + endTime.get(ChronoField.MINUTE_OF_DAY) + " from restaurants where name = '" + name + "';");

            });
        });
    }
}
