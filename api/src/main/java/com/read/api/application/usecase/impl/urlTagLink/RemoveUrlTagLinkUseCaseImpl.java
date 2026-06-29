package com.read.api.application.usecase.impl.urlTagLink;

import com.read.api.application.usecase.base.UseCase;
import com.read.api.application.usecase.interfaces.urlTagLink.RemoveUrlTagLinkUseCase;
import com.read.api.domain.model.UrlModel;
import com.read.api.domain.model.UrlTagModel;
import com.read.api.domain.repository.UrlRepository;
import com.read.api.domain.repository.UrlTagRepository;
import com.read.api.utils.metrics.observed.ObservedMetric;
import com.read.api.utils.result.Result;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@UseCase
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RemoveUrlTagLinkUseCaseImpl implements RemoveUrlTagLinkUseCase {

    UrlRepository urlRepository;
    UrlTagRepository tagRepository;

    @Override
    @Retry(name = "remove")
    @ObservedMetric("url.tag.link.remove")
    public Result<UrlModel> execute(Long urlId, Long tagId) {

        UrlModel url = urlRepository.findById(urlId).orElse(null);

        if (url == null) {
            return Result.failure(
                    "Url not found",
                    404
            );
        }

        UrlTagModel tag =
                tagRepository.findById(tagId)
                        .orElse(null);

        if (tag == null) {
            return Result.failure(
                    "Tag not found",
                    404
            );
        }

        if (!url.getTags().contains(tag.getName())) {
            return Result.success(
                    url,
                    200
            );
        }

        url.removeTag(tag.getName());

        url.getMetric().decrementTagCount();
        UrlModel saved = urlRepository.save(url);

        return Result.success(
                saved,
                200
        );
    }
}