package io.github.scottleedavis.mattermost.remind.reminders;

import io.github.scottleedavis.mattermost.remind.exceptions.ParserException;
import io.github.scottleedavis.mattermost.remind.messages.ParsedRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

@Component
public class Parser {

    @Autowired
    private Formatter formatter;

    private static class WhenPattern {
        public String raw;
        public String normalized;
    }

    public ParsedRequest extract(String text) throws Exception {
        ParsedRequest parsedRequest = new ParsedRequest();

        parsedRequest.setTarget(findTarget(text));
        text = text.replaceFirst(parsedRequest.getTarget(), "").trim();
        if (text.length() == 0)
            return parsedRequest;

        boolean hasQuotes = true;
        try {
            String message = findMessage(text);
            if (message.charAt(0) == '"' && message.charAt(message.length() - 1) == '"')
                parsedRequest.setMessage(message.substring(1, message.length() - 1));
            else
                parsedRequest.setMessage(message);
            text = text.replace(message, "");
        } catch (Exception e) {
            hasQuotes = false;
        }

        WhenPattern whenPattern = findWhen(text);
        parsedRequest.setWhen(whenPattern.normalized);
        text = text.replaceAll(whenPattern.raw, "").trim();

        if (!hasQuotes)
            parsedRequest.setMessage(text);

        return parsedRequest;
    }

    private String findTarget(String text) throws Exception {

        String[] parts = text.split("\\s+");

        if (parts[0].equals(""))
            throw new Exception("Empty target");
        else if (parts[0].equals("me"))
            return parts[0];
        else if (parts[0].equals("list"))
            return parts[0];
        else if (parts[0].equals("help"))
            return parts[0];
        else if (parts[0].equals("version"))
            return parts[0];
        else if (parts[0].charAt(0) == '@')
            return parts[0];
        else if (parts[0].charAt(0) == '~')
            return parts[0];

        throw new ParserException("Unrecognized target");

    }

    private String findMessage(String text) throws Exception {
        List<Integer> indexes = new ArrayList<>();
        IntStream.range(0, text.length())
                .forEach(index -> {
                    if (text.charAt(index) == '"')
                        indexes.add(index);
                });
        if (indexes.size() >= 2) {
            int min = Collections.min(indexes);
            int max = Collections.max(indexes);
            return text.substring(min, max + 1);
        }

        throw new ParserException("Could not determine message.");

    }

    private WhenPattern findWhen(String text) throws Exception {

        WhenPattern whenPattern = new WhenPattern();
        int subStringA = -1;
        int subStringB = -1;

        subStringA = text.indexOf(" in ");
        if (subStringA > -1) {
            whenPattern.raw = whenPattern.normalized = text.substring(subStringA).trim();
            return whenPattern;
        }

        subStringA = text.indexOf(" every ");
        subStringB = text.indexOf(" at ");
        if ((subStringA > -1 && subStringB == -1) || (subStringB > subStringA) && subStringA != -1) {
            whenPattern.raw = whenPattern.normalized = text.substring(subStringA).trim();
            return whenPattern;
        }

        subStringA = text.indexOf(" on ");
        if (subStringA > -1) {
            whenPattern.raw = whenPattern.normalized = text.substring(subStringA).trim();
            return whenPattern;
        }

        subStringA = text.indexOf(" everyday ");
        subStringB = text.indexOf(" at ");
        if ((subStringA > -1 && subStringB >= -1) && (subStringB > subStringA)) {
            whenPattern.raw = whenPattern.normalized = text.substring(subStringA).trim();
            return whenPattern;
        }
        subStringA = text.indexOf(" tomorrow ");
        subStringB = text.indexOf(" at ");
        if ((subStringA > -1 && subStringB >= -1) && (subStringB > subStringA)) {
            whenPattern.raw = whenPattern.normalized = text.substring(subStringA).trim();
            return whenPattern;
        }
        subStringA = text.indexOf(" monday ");
        subStringB = text.indexOf(" at ");
        if ((subStringA > -1 && subStringB >= -1) && (subStringB > subStringA)) {
            whenPattern.raw = whenPattern.normalized = text.substring(subStringA).trim();
            return whenPattern;
        }
        subStringA = text.indexOf(" tuesday ");
        subStringB = text.indexOf(" at ");
        if ((subStringA > -1 && subStringB >= -1) && (subStringB > subStringA)) {
            whenPattern.raw = whenPattern.normalized = text.substring(subStringA).trim();
            return whenPattern;
        }
        subStringA = text.indexOf(" wednesday ");
        subStringB = text.indexOf(" at ");
        if ((subStringA > -1 && subStringB >= -1) && (subStringB > subStringA)) {
            whenPattern.raw = whenPattern.normalized = text.substring(subStringA).trim();
            return whenPattern;
        }
        subStringA = text.indexOf(" thursday ");
        subStringB = text.indexOf(" at ");
        if ((subStringA > -1 && subStringB >= -1) && (subStringB > subStringA)) {
            whenPattern.raw = whenPattern.normalized = text.substring(subStringA).trim();
            return whenPattern;
        }
        subStringA = text.indexOf(" friday ");
        subStringB = text.indexOf(" at ");
        if ((subStringA > -1 && subStringB >= -1) && (subStringB > subStringA)) {
            whenPattern.raw = whenPattern.normalized = text.substring(subStringA).trim();
            return whenPattern;
        }
        subStringA = text.indexOf(" saturday ");
        subStringB = text.indexOf(" at ");
        if ((subStringA > -1 && subStringB >= -1) && (subStringB > subStringA)) {
            whenPattern.raw = whenPattern.normalized = text.substring(subStringA).trim();
            return whenPattern;
        }
        subStringA = text.indexOf(" sunday ");
        subStringB = text.indexOf(" at ");
        if ((subStringA > -1 && subStringB >= -1) && (subStringB > subStringA)) {
            whenPattern.raw = whenPattern.normalized = text.substring(subStringA).trim();
            return whenPattern;
        }

        subStringA = text.indexOf(" at ");
        subStringB = text.indexOf(" every ");
        if ((subStringA > -1 && subStringB == -1) || (subStringB > subStringA) && subStringA != -1) {
            whenPattern.raw = whenPattern.normalized = text.substring(subStringA).trim();
            return whenPattern;
        }

        String[] textSplit = text.split(" ");

        // dates <month> <day>
        String lastWord = textSplit[textSplit.length - 2] + " " + textSplit[textSplit.length - 1];
        try {

            whenPattern.raw = lastWord;
            whenPattern.normalized = formatter.capitalize(formatter.normalizeDate(lastWord));
            return whenPattern;

        } catch (Exception e) {

            // tomorrow
            lastWord = textSplit[textSplit.length - 1];
            switch (lastWord.toLowerCase()) {
                case "tomorrow":
                    whenPattern.raw = lastWord;
                    whenPattern.normalized = formatter.capitalize(lastWord);
                    return whenPattern;
                case "everyday":
                case "mondays":
                case "tuesdays":
                case "wednesdays":
                case "thursdays":
                case "fridays":
                case "saturdays":
                case "sundays":
                    whenPattern.raw = lastWord;
                    whenPattern.normalized = formatter.capitalize(lastWord);
                    return whenPattern;
                default:
                    break;
            }

            try {

                whenPattern.raw = lastWord;
                whenPattern.normalized = formatter.capitalize(formatter.normalizeDate(lastWord));
                return whenPattern;
            } catch (Exception er) {
                throw new ParserException("No when found", er);
            }
        }
    }
}
