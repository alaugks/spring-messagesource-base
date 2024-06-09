package io.github.alaugks.spring.messagesource.catalog.ressources;

import io.github.alaugks.spring.messagesource.catalog.exception.CatalogMessageSourceRuntimeException;
import io.github.alaugks.spring.messagesource.catalog.records.Filename;
import io.github.alaugks.spring.messagesource.catalog.records.TranslationFile;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.util.Assert;

public final class ResourcesLoader {

    private final Locale defaultLocale;
    private final Set<String> basenames;
    private final List<String> fileExtensions;

    public ResourcesLoader(Locale defaultLocale, Set<String> basenames, List<String> fileExtensions) {
        Assert.notNull(defaultLocale, "defaultLocale locale must not be null");
        Assert.notNull(basenames, "basenames must not be null");
        Assert.notNull(fileExtensions, "fileExtensions must not be null");

        this.defaultLocale = defaultLocale;
        this.basenames = basenames;
        this.fileExtensions = fileExtensions;
    }

    public List<TranslationFile> getTranslationFiles() {
        try {
            ArrayList<TranslationFile> translationTranslationFiles = new ArrayList<>();
            PathMatchingResourcePatternResolver resourceLoader = new PathMatchingResourcePatternResolver();
            for (String basename : getBasenameSet()) {
                Resource[] resources = resourceLoader.getResources(basename);
                for (Resource resource : resources) {
                    if (this.isFileExtensionSupported(resource)) {
                        TranslationFile translationFile = this.parseFileName(resource);
                        if (translationFile != null) {
                            translationTranslationFiles.add(translationFile);
                        }
                    }
                }
            }

            return translationTranslationFiles;
        } catch (IOException e) {
            throw new CatalogMessageSourceRuntimeException(e);
        }
    }

    private TranslationFile parseFileName(Resource resource) throws IOException {
        Filename filename = new ResourcesFileNameParser(resource.getFilename()).parse();
        if (filename != null) {
            return new TranslationFile(
                filename.domain(),
                filename.hasLocale()
                    ? filename.locale()
                    : this.defaultLocale,
                resource.getInputStream()
            );
        }
        return null;
    }

    private boolean isFileExtensionSupported(Resource resource) {
        String fileName = resource.getFilename();
        return fileName != null && fileExtensions.contains(fileName.substring(fileName.lastIndexOf(".") + 1));
    }

    private Set<String> getBasenameSet() {
        return this.basenames;
    }
}
