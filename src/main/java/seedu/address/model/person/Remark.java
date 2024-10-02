package seedu.address.model.person;

import static java.util.Objects.requireNonNull;

/**
 * Represents remark on a person in the address book
 * Guarantees: immutable; is always valid
 */
public class Remark {

    public final String value;

    public Remark(String remark) {
        requireNonNull(remark);
        value = remark;
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof Remark)) {
            return false;
        }

        Remark otherRemark = (Remark) other;
        return value.equals(otherRemark.value);
    }
}
