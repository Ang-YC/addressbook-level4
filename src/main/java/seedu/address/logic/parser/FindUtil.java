package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_MAJOR;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;

import java.util.function.Predicate;

import seedu.address.logic.commands.FindCommand;
import seedu.address.logic.parser.exceptions.ParseException;

import seedu.address.model.person.AllPredicate;
import seedu.address.model.person.Person;

/**
 * Contains utility methods used for FindCommandParser
 */
public class FindUtil {

    /**
     * Parses the string {@code trimmedArgs} and {@code argMultimap} to form a combined Predicate based on user request
     * @param trimmedArgs,argMultimap
     * @return the predicate user demanded
     * @throws ParseException
     */
    public static Predicate<Person> parseFindArgs(String trimmedArgs, ArgumentMultimap argMultimap)
            throws ParseException {
        requireNonNull(trimmedArgs);
        Predicate<Person> finalPredicate;

        // no prefix used, search for all fields (global search)
        if (!startWithPrefix(trimmedArgs)) {
            String[] keywords = trimmedArgs.split("\\s+");

            try {
                AllPredicate allPredicate = PredicateUtil.parseAllPredicates(keywords);
                finalPredicate = PredicateUtil.formOrPredicate(allPredicate.getNamePredicate().getPredicate(),
                        allPredicate.getPhonePredicate().getPredicate(),
                        allPredicate.getEmailPredicate().getPredicate(),
                        allPredicate.getAddressPredicate().getPredicate(),
                        allPredicate.getMajorPredicate().getPredicate());
                return finalPredicate;
            } catch (ParseException pe) {
                throw new ParseException(pe.getMessage(), pe);
            }

        } else {
            // at least one prefix is used, search for fields that matches prefix only
            if (!argMultimap.getPreamble().isEmpty()) {
                throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
            }

            try {
                AllPredicate allPredicate = PredicateUtil.parseSelectedPredicates(argMultimap);
                finalPredicate = PredicateUtil.formAndPredicate(allPredicate.getNamePredicate().getPredicate(),
                        allPredicate.getPhonePredicate().getPredicate(),
                        allPredicate.getEmailPredicate().getPredicate(),
                        allPredicate.getAddressPredicate().getPredicate(),
                        allPredicate.getMajorPredicate().getPredicate());
                return finalPredicate;
            } catch (ParseException pe) {
                throw new ParseException(pe.getMessage(), pe);
            }

        }
    }

    /**
     * Parses the string {@code trimmedArgs} and a returns a boolean value true if prefix is present
     * @param trimmedArgs
     * @return boolean value
     */
    private static boolean startWithPrefix(String trimmedArgs) {
        String[] args = trimmedArgs.split("\\s+");

        return (args[0].contains(PREFIX_NAME.toString())
                || args[0].contains(PREFIX_PHONE.toString())
                || args[0].contains(PREFIX_EMAIL.toString())
                || args[0].contains(PREFIX_ADDRESS.toString())
                || args[0].contains(PREFIX_MAJOR.toString())); // more fields to be added if necessary
    }
}