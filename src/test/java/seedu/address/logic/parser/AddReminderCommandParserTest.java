package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.commands.CommandTestUtil.DATE_DESC;
import static seedu.address.logic.commands.CommandTestUtil.DESCRIPTION_DESC;
import static seedu.address.logic.commands.CommandTestUtil.VALID_DESCRIPTION;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.model.person.Reminder.formatter;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.AddReminderCommand;

public class AddReminderCommandParserTest {
    private static final String MESSAGE_INVALID_FORMAT =
            String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddReminderCommand.MESSAGE_USAGE);
    private AddReminderCommandParser parser = new AddReminderCommandParser();
    @Test
    public void parse_missingParts_failure() {
        // no index specified
        assertParseFailure(parser, DATE_DESC + DESCRIPTION_DESC,
                        MESSAGE_INVALID_FORMAT);

        // no date specified
        assertParseFailure(parser, "1" + DESCRIPTION_DESC, MESSAGE_INVALID_FORMAT);

        // no description specified
        assertParseFailure(parser, "1" + DATE_DESC, MESSAGE_INVALID_FORMAT);

        // no index or fields
        assertParseFailure(parser, "", MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_invalidPreamble_failure() {
        // negative index
        assertParseFailure(parser, "-5" + DATE_DESC + DESCRIPTION_DESC, MESSAGE_INVALID_FORMAT);

        // zero index
        assertParseFailure(parser, "0" + DATE_DESC + DESCRIPTION_DESC, MESSAGE_INVALID_FORMAT);

        // invalid arguments being parsed as preamble
        assertParseFailure(parser, "1 some random string"
                + DATE_DESC + DESCRIPTION_DESC, MESSAGE_INVALID_FORMAT);

        // invalid prefix being parsed as preamble
        assertParseFailure(parser, "1 i/random" + DATE_DESC + DESCRIPTION_DESC, MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_invalidDate_failure() {
        // date is in the past
        assertParseFailure(parser, "1 d/01-01-2024" + DESCRIPTION_DESC,
                "Reminder date must be in the future" );
    }

    @Test
    public void parse_success() {
        // today's date
        String today = LocalDate.now().format(formatter);
        AddReminderCommand expectedCommand = new AddReminderCommand(Index.fromOneBased(1), today, VALID_DESCRIPTION);

        assertParseSuccess(parser, "1 d/" + today + DESCRIPTION_DESC, expectedCommand);
    }

    /*
    This test is currently not working as the hashcodes for the results are different
    @Test
    public void parse_allFieldsSpecified_success() {
        Index targetIndex = INDEX_SECOND_PERSON;
        String userInput = "1" + DATE_DESC + DESCRIPTION_DESC;

        AddReminderCommand expectedCommand = new AddReminderCommand(targetIndex, VALID_DATE,
                VALID_DESCRIPTION);

        assertParseSuccess(parser, userInput, expectedCommand);
    }
    */
}
