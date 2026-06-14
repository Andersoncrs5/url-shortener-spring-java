package com.read.api.infrastructure.persistence.repository;

import com.mongodb.client.result.DeleteResult;
import com.read.api.api.dto.user.UserFilter;
import com.read.api.domain.model.UserModel;
import com.read.api.domain.repository.UserRepository;
import com.read.api.infrastructure.persistence.entity.UserEntity;
import com.read.api.infrastructure.persistence.mapper.UserMapperRepository;
import com.read.api.infrastructure.persistence.mongo.MongoUserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserRepositoryImpl implements UserRepository {

    MongoUserRepository repository;
    MongoTemplate template;
    UserMapperRepository mapper;

    @Override
    public UserModel save(UserModel user) {
        var entity = mapper.toEntity(user);

        return mapper.toModel(
                repository.save(entity)
        );
    }

    @Override
    public UserModel insert(UserModel user) {

        var entity = mapper.toEntity(user);

        return mapper.toModel(
                repository.insert(entity)
        );
    }
    @Override
    public Optional<UserModel> findById(Long id) {
        return repository.findById(id).map(mapper::toModel);
    }

    @Override
    public boolean existsById(Long id) {

        Query query = Query.query(
                Criteria.where("id").is(id)
        );

        return template.exists(
                query,
                UserEntity.class
        );
    }

    @Override
    public int deleteById(Long id) {
        Query query = Query.query(
                Criteria.where("id").is(id)
        );

        DeleteResult result = template.remove(
                query,
                UserEntity.class
        );

        return (int) result.getDeletedCount();
    }

    @Override
    public Page<UserModel> findAll(UserFilter filter, Pageable pageable) {

        Query query = new Query();

        if (filter.getId() != null) {
            query.addCriteria(
                    Criteria.where("id").is(filter.getId())
            );
        }

        if (filter.getName() != null && !filter.getName().isBlank()) {
            query.addCriteria(
                    Criteria.where("name")
                            .regex(filter.getName(), "i")
            );
        }

        if (filter.getEmail() != null && !filter.getEmail().isBlank()) {
            query.addCriteria(
                    Criteria.where("email")
                            .regex(filter.getEmail(), "i")
            );
        }

        if (filter.getActive() != null) {
            query.addCriteria(
                    Criteria.where("active")
                            .is(filter.getActive())
            );
        }

        if (filter.getCreatedAtAfter() != null) {
            query.addCriteria(
                    Criteria.where("createdAt")
                            .gte(filter.getCreatedAtAfter())
            );
        }

        if (filter.getCreatedAtBefore() != null) {
            query.addCriteria(
                    Criteria.where("createdAt")
                            .lte(filter.getCreatedAtBefore())
            );
        }

        if (filter.getUpdatedAtAfter() != null) {
            query.addCriteria(
                    Criteria.where("updatedAt")
                            .gte(filter.getUpdatedAtAfter())
            );
        }

        if (filter.getUpdatedAtBefore() != null) {
            query.addCriteria(
                    Criteria.where("updatedAt")
                            .lte(filter.getUpdatedAtBefore())
            );
        }

        long total = template.count(query, UserEntity.class);

        query.with(pageable);

        List<UserModel> content = template
                .find(query, UserEntity.class)
                .stream()
                .map(mapper::toModel)
                .toList();

        return new PageImpl<>(
                content,
                pageable,
                total
        );
    }

    @Override
    public Optional<UserModel> findByEmailIgnoreCase(String email) {
        return this.repository.findByEmailIgnoreCase(email);
    }

    @Override
    public boolean existsByEmailIgnoreCase(String email) {
        return this.repository.existsByEmailIgnoreCase(email);
    }

    @Override
    public boolean existsByNameIgnoreCase(String name) {
        return this.repository.existsByNameIgnoreCase(name);
    }
}
