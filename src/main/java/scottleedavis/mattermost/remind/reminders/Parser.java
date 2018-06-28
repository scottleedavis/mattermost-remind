package scottleedavis.mattermost.remind.reminders;

import org.springframework.stereotype.Component;
import scottleedavis.mattermost.remind.messages.ParsedRequest;

@Component
public class Parser {

    public ParsedRequest extract(String text) throws Exception {
        ParsedRequest parsedRequest = new ParsedRequest();
        parsedRequest.setTarget(findTarget(text));
        text = text.replace(parsedRequest.getTarget(),"").trim();
        if( text.length() == 0 )
            return parsedRequest;
        parsedRequest.setWhen(findWhen(text));
        text = text.replace(parsedRequest.getWhen(),"").trim();
        parsedRequest.setMessage(text);
        return parsedRequest;
    }

    private String findTarget(String text) throws Exception {

        String[] parts = text.split("\\s+");

        if (parts[0].equals("") )
            throw new Exception("Empty target");
        else if (parts[0].equals("me") )
            return parts[0];
        else if (parts[0].equals("list") )
            return parts[0];
        else if (parts[0].equals("help") )
            return parts[0];
        else if (parts[0].charAt(0) == '@')
            return parts[0];
        else if (parts[0].charAt(0) == '#')
            return parts[0];

        throw new Exception("Unrecognized target");

    }

    private String findWhen(String text) throws Exception {
        //find in, at, on, every starting from the back
        int subString = -1;

        subString = text.indexOf(" at ");
        if( subString > -1 ) {
            return text.substring(subString).trim();
        }

        subString = text.indexOf(" in ");
        if( subString > -1 ) {
            return text.substring(subString).trim();
        }

        subString = text.indexOf(" on ");
        if( subString > -1 ) {
            return text.substring(subString).trim();
        }

        subString = text.indexOf(" every ");
        if( subString > -1 ) {
            return text.substring(subString).trim();
        }

        throw new Exception("No when found");

    }
}
