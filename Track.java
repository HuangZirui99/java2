import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Track {
    // A list that stores Points
    List<Point> trackList;

    //  A default constructor that creates a track containing no points
    public Track() {
        this.trackList = new ArrayList<>();
    }

    public void readFile(String filename) throws FileNotFoundException {
        if (!trackList.isEmpty()) {
            trackList.clear();
        }
        if (filename == null) throw new IllegalArgumentException("The file name is null");
        File file = new File(filename);
        FileInputStream fis = new FileInputStream(file);
        Scanner scanner = new Scanner(new BufferedInputStream(fis));
        scanner.nextLine();
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String[] lineSplit = line.split(",");
            if (lineSplit.length < 4) {
                throw new GPSException("Don't have enough arguments!");
            }
            ZonedDateTime timestamp = ZonedDateTime.parse(lineSplit[0]);
            double longitude = Double.parseDouble(lineSplit[1]);
            double latitude = Double.parseDouble(lineSplit[2]);
            double elevation = Double.parseDouble(lineSplit[3]);
            Point point = new Point(timestamp, longitude, latitude, elevation);
            this.trackList.add(point);
        }
    }

    public void add(Point point) {
        this.trackList.add(point);
    }

    public int size() {
        return this.trackList.size();
    }

    public Point get(int position) {
        if (position < 0 || position >= size()) {
            throw new GPSException("The Point position is invalid");
        }
        return this.trackList.get(position);
    }

    public Point lowestPoint() {
        if (this.trackList.size() <= 1) {
            throw new GPSException("Don't have enough point");
        }
        int lowestIndex = 0;
        double lowestElevations = Double.MAX_VALUE;
        for (int i = 0; i < size(); i++) {
            if (trackList.get(i).getElevation() < lowestElevations) {
                lowestIndex = i;
                lowestElevations = trackList.get(i).getElevation();
            }
        }
        return trackList.get(lowestIndex);
    }

    public Point highestPoint() {
        if (size() <= 0) {
            throw new GPSException("the track is empty");
        }
        int highestIndex = 0;
        double hightestElevations = Double.MIN_VALUE;
        for (int i = 0; i < size(); i++) {
            if (trackList.get(i).getElevation() > hightestElevations) {
                highestIndex = i;
                hightestElevations = trackList.get(i).getElevation();
            }
        }
        return trackList.get(highestIndex);
    }

    public double totalDistance() {
        if (size() <= 1) {
            throw new GPSException("the track is empty");
        }

        double total_distance = 0;
        for (int i = 0; i < size() - 1; i++) {
            Point p = trackList.get(i);
            Point q = trackList.get(i + 1);
            double distance = Point.greatCircleDistance(p, q);
            total_distance += distance;
        }
        return total_distance;
    }

    public double averageSpeed() {
        if (size() <= 1) {
            throw new GPSException("the track is empty");
        }

        long secondsBetween = ChronoUnit.SECONDS.between(trackList.get(size() - 1).getTime(), trackList.get(0).getTime());
        return totalDistance() / (-secondsBetween);
    }

    public static void main(String[] args) {

    }

}
