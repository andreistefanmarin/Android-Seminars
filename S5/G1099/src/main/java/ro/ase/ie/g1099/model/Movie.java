package ro.ase.ie.g1099.model;

import java.util.Date;

public class Movie {
    private String title; //EditText
    private String genre; //Spinner
    private Date release; //DatePicker | EditText
    private double budget; //EditText numeric
    private float rating; // RatingBar
    private String guidance; // RadioGroup | RadioButton
    private byte duration; //SeekBar
    private boolean watched; //Switch
    private String posterUrl; //

    public Movie(String title, String genre, Date release, double budget, float rating, String guidance, byte duration, boolean watched, String posterUrl) {
        this.title = title;
        this.genre = genre;
        this.release = release;
        this.budget = budget;
        this.rating = rating;
        this.guidance = guidance;
        this.duration = duration;
        this.watched = watched;
        this.posterUrl = posterUrl;
    }
}
