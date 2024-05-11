package io.github.alaugks.spring.messagesource.base.ressources;

import io.github.alaugks.spring.messagesource.base.records.Filename;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.util.Assert;

final class ResourcesFileNameParser {

    private final String filename;

    public ResourcesFileNameParser(String filename) {
        Assert.notNull(filename, "filename must not be null");
        this.filename = filename;
    }

    public Filename parse() {
        String regexp = "^(?<domain>[a-z0-9]+)(?:([_-](?<language>[a-z]+))(?:[_-](?<region>[a-z]+))?)?";
        Pattern pattern = Pattern.compile(regexp, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(this.filename);

        if (matcher.find()) {
            return new Filename(
                matcher.group("domain"),
                matcher.group("language"),
                matcher.group("region")
            );
        }

        return null;
    }
}
