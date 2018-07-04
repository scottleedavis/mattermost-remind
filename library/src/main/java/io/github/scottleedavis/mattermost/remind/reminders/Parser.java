package io.github.scottleedavis.mattermost.remind.reminders;

import io.github.scottleedavis.mattermost.remind.exceptions.ParserException;
import io.github.scottleedavis.mattermost.remind.messages.ParsedRequest;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

@Component
public class Parser {

    public ParsedRequest extract(String text) throws Exception {
        ParsedRequest parsedRequest = new ParsedRequest();
        parsedRequest.setTarget(findTarget(text));
        text = text.replace(parsedRequest.getTarget(), "").trim();
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
        parsedRequest.setWhen(findWhen(text));
        text = text.replace(parsedRequest.getWhen(), "").trim();
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
        else if (parts[0].charAt(0) == '#')
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

    private String findWhen(String text) throws Exception {
        int subString = -1;

        subString = text.indexOf(" in ");
        if (subString > -1) {
            return text.substring(subString).trim();
        }

        subString = text.indexOf(" every ");
        if (subString > -1) {
            return text.substring(subString).trim();
        }

        subString = text.indexOf(" on ");
        if (subString > -1) {
            return text.substring(subString).trim();
        }

        subString = text.indexOf(" at ");
        if (subString > -1) {
            return text.substring(subString).trim();
        }

        throw new ParserException("No when found");

    }
}
