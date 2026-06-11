package com.read.api.infrastructure.persistence.repository;

import com.mongodb.client.result.DeleteResult;
import com.read.api.domain.model.RoleModel;
import com.read.api.domain.repository.RoleRepository;
import com.read.api.infrastructure.persistence.entity.RoleEntity;
import com.read.api.infrastructure.persistence.mapper.RoleMapperRepository;
import com.read.api.infrastructure.persistence.mongo.MongoRoleRepository;
import com.read.api.api.dto.role.RoleFilter;
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
public class RoleRepositoryImpl implements RoleRepository {

    MongoTemplate template;
    MongoRoleRepository repository;
    RoleMapperRepository mapper;

    @Override
    public RoleModel save(RoleModel role) {
        var entity = mapper.toEntity(role);

        return mapper.toModel(
                repository.save(entity)
        );
    }

    @Override
    public RoleModel insert(RoleModel role) {
        var entity = mapper.toEntity(role);

        return mapper.toModel(
                repository.insert(entity)
        );
    }

    @Override
    public Optional<RoleModel> findById(Long id) {
        return repository.findById(id)
                .map(mapper::toModel);
    }

    @Override
    public boolean existsById(Long id) {
        Query query = Query.query(
                Criteria.where("id").is(id)
        );

        return template.exists(query, RoleEntity.class);
    }

    @Override
    public int deleteById(Long id) {
        Query query = Query.query(
                Criteria.where("id").is(id)
        );

        DeleteResult result = template.remove(query, RoleEntity.class);

        return (int) result.getDeletedCount();
    }

    @Override
    public Page<RoleModel> findAll(RoleFilter filter, Pageable pageable) {

        Query query = new Query();

        if (filter.getId() != null) {
            query.addCriteria(Criteria.where("id").is(filter.getId()));
        }

        if (filter.getName() != null && !filter.getName().isBlank()) {
            query.addCriteria(
                    Criteria.where("name").regex(filter.getName(), "i")
            );
        }

        if (filter.getDescription() != null && !filter.getDescription().isBlank()) {
            query.addCriteria(
                    Criteria.where("description").regex(filter.getDescription(), "i")
            );
        }

        if (filter.getActive() != null)
            query.addCriteria(Criteria.where("active").is(filter.getActive()));

        if (filter.getCreatedAtAfter() != null) {
            query.addCriteria(
                    Criteria.where("createdAt").gte(filter.getCreatedAtAfter())
            );
        }

        if (filter.getCreatedAtBefore() != null) {
            query.addCriteria(
                    Criteria.where("createdAt").lte(filter.getCreatedAtBefore())
            );
        }

        if (filter.getUpdatedAtAfter() != null) {
            query.addCriteria(
                    Criteria.where("updatedAt").gte(filter.getUpdatedAtAfter())
            );
        }

        if (filter.getUpdatedAtBefore() != null) {
            query.addCriteria(
                    Criteria.where("updatedAt").lte(filter.getUpdatedAtBefore())
            );
        }

        long total = template.count(query, RoleEntity.class);

        query.with(pageable);

        List<RoleModel> content = template.find(query, RoleEntity.class)
                .stream()
                .map(mapper::toModel)
                .toList();

        return new PageImpl<>(content, pageable, total);
    }
}