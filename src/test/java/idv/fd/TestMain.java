package idv.fd;

import idv.fd.restaurant.model.OpenHours;
import idv.fd.etl.OpenHoursDataParser;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

public class TestMain {

    public static void main(String[] args) {

        //parseLine();
        //parseTime();

    }

    static void parseTime() {

        LocalTime lt = OpenHours.parseTime("5:00");
        System.out.println("time: " + lt);
        lt = OpenHours.parseTime("15:59");
        System.out.println("time: " + lt);
    }

    static void parseLine() {

        String[] lines = {
                "\"Plumed Horse\",\"Mon 11:45 am - 9:15 pm / Tues 7:45 am - 12:30 pm / Weds - Thurs, Sun 7:45 am - 3:45 pm / Fri 7 am - 3:45 am / Sat 6 am - 3:30 pm\"",
                "\"Everest\",\"Mon, Weds 5:30 am - 3:30 am / Tues 1:30 pm - 4 pm / Thurs 3 pm - 12:15 pm / Fri 1 pm - 2 pm / Sat 7:45 am - 12 pm / Sun 11:15 am - 7:45 pm\"",
                "\"Burger & Beer Joint\",\"Mon - Tues 3 pm - 2 am / Weds - Thurs 2 pm - 11:15 pm / Fri - Sat 5:15 am - 7 am / Sun 7:45 am - 9:30 pm\"",
                "\"Kushi Tsuru\",\"Mon-Sun 11:30 am - 9 pm\"",
                "\"'Ulu Ocean Grill and Sushi Lounge\",\"Mon, Fri 2:30 pm - 8 pm / Tues 11 am - 2 pm / Weds 1:15 pm - 3:15 am / Thurs 10 am - 3:15 am / Sat 5 am - 11:30 am / Sun 10:45 am - 5 pm\""
        };

        OpenHoursDataParser parser = new OpenHoursDataParser();

        Arrays.stream(lines).forEach(line -> {
            parser.parseLine(line).subscribe(re -> System.out.println(re.getOpenHours()));
        });
    }

    static void parseFile() {

        OpenHoursDataParser parser = new OpenHoursDataParser();
        parser.parseFile("./data/hours.csv").subscribe(System.out::println);
    }
}
