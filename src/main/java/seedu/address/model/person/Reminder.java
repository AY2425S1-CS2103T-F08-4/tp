package seedu.address.model.person;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

public class Reminder {
    public static final String MESSAGE_CONSTRAINTS_DATE = "Date should be in the format DD-MM-YYYY";
    public static final String MESSAGE_CONSTRAINTS_DESCRIPTION = "Description should not be empty";
    public static final String VALIDATION_REGEX = "[^\\s].*";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    public final LocalDate reminderDate;
    public final String reminderDescription;
    public final Name personToMeet;

    /**
     * Constructs a {@code Reminder}
     *
     * @param date A valid date
     * @param description A valid description
     * @param personToMeet A valid name of the person that the reminder is linked to
     */
    public Reminder(String date, String description, Name personToMeet) {
        requireNonNull(date, description);
        requireNonNull(personToMeet);
        checkArgument(isValidDate(date), MESSAGE_CONSTRAINTS_DATE);
        checkArgument(isValidDescription(description), MESSAGE_CONSTRAINTS_DESCRIPTION);
        this.reminderDate = LocalDate.parse(date, formatter);
        this.reminderDescription = description;
        this.personToMeet = personToMeet;
    }

    public static boolean isValidDate(String test) {
        try {
            LocalDate.parse(test, formatter);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    public static boolean isValidDescription(String test) {
        return test.matches(VALIDATION_REGEX);
    }

    /**
     * Getter for reminderDate
     *
     * @return reminderDate of Reminder
     */
    public LocalDate getReminderDate() {
        return reminderDate;
    }

    /**
     * Getter for reminderDescription
     *
     * @return reminderDescription of reminder
     */
    public String getReminderDescription() {
        return reminderDescription;
    }

    /**
     * Getter for personToMeet
     *
     * @return personToMeet of Reminder
     */
    public Name getPersonToMeet() {
        return personToMeet;
    }
    @Override
    public String toString() {
        return reminderDate.toString() + ": " + reminderDescription;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof Reminder)) {
            return false;
        }

        Reminder otherReminder = (Reminder) other;
        return reminderDescription.equals(otherReminder.reminderDescription)
                && reminderDate.equals(otherReminder.reminderDate)
                && personToMeet.equals(otherReminder.personToMeet);
    }

    @Override
    public int hashCode() {
        return reminderDate.hashCode();
    }
}
