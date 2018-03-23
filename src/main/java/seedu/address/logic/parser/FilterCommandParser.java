package seedu.address.logic.parser;

import static seedu.address.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EXPECTED_GRADUATION_YEAR;
import static seedu.address.logic.parser.CliSyntax.PREFIX_RATING;
import static seedu.address.logic.parser.CliSyntax.PREFIX_RESUME;

import java.util.function.Predicate;
import java.util.stream.Stream;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.logic.commands.FilterCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.Person;

/**
 * Parses input arguments and creates a new FilterCommand object
 */
public class FilterCommandParser implements Parser<FilterCommand> {
    /**
     * Parses the given {@code String} of arguments in the context of the AddCommand
     * and returns an AddCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public FilterCommand parse(String args) throws ParseException {
        ArgumentMultimap argMultimap =
                ArgumentTokenizer.tokenize(args, PREFIX_EXPECTED_GRADUATION_YEAR, PREFIX_RATING); //PREFIX_TAG temporarily removed

        if (!isValidFilterCommandInput(argMultimap, PREFIX_EXPECTED_GRADUATION_YEAR, PREFIX_RATING)
                || !argMultimap.getPreamble().isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, FilterCommand.MESSAGE_USAGE));
        }

        try {
            Predicate<Person> expectedGraduationYearPredicate = FilterUtil.parseExpectedGraduationYear(argMultimap
                    .getValue(PREFIX_EXPECTED_GRADUATION_YEAR));
            Predicate<Person> ratingPredicate = FilterUtil.parseRating(argMultimap.getValue(PREFIX_RATING));
            // combine all predicates together using and
            Predicate<Person> combinedPredicate = combinePredicate(expectedGraduationYearPredicate, ratingPredicate);
            return new FilterCommand(combinedPredicate);
        } catch (ParseException pe) {
            throw pe;
        } catch (IllegalValueException ive) {
            throw new ParseException(ive.getMessage(), ive);
        }
    }

    private boolean isValidFilterCommandInput(ArgumentMultimap argumentMultimap, Prefix... prefixes) {
        boolean hasAnyPrefixes = false;
        for (Prefix p: prefixes) {
            if (arePrefixesPresent(argumentMultimap, p)) {
                hasAnyPrefixes = true;
                break;
            }
        }
        return hasAnyPrefixes;
    }
    private Predicate<Person> combinePredicate(Predicate<Person>... predicates) {
        Predicate<Person> combinedPredicate = null;
        for(Predicate<Person> p: predicates) {
            if (p == null) {
                continue;
            } else {
                if (combinedPredicate == null) {
                    combinedPredicate = p;
                } else {
                    combinedPredicate = combinedPredicate.and(p);
                }
            }
        }
        return combinedPredicate;
    }

    /**
     * Returns true if none of the prefixes contains empty {@code Optional} values in the given
     * {@code ArgumentMultimap}.
     */
    private static boolean arePrefixesPresent(ArgumentMultimap argumentMultimap, Prefix... prefixes) {
        return Stream.of(prefixes).allMatch(prefix -> argumentMultimap.getValue(prefix).isPresent());
    }
}
