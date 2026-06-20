package com.read.api.infrastructure.persistence.repository;

import com.read.api.api.dto.role.RoleFilter;
import com.read.api.domain.model.RoleModel;
import com.read.api.domain.repository.RoleRepository;
import com.read.api.infrastructure.persistence.base.BaseRepositoryImpl;
import com.read.api.infrastructure.persistence.entity.RoleEntity;
import com.read.api.infrastructure.persistence.mapper.RoleMapperRepository;
import com.read.api.infrastructure.persistence.mongo.MongoRoleRepository;
import com.read.api.infrastructure.persistence.utils.QueryUtils;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoleRepositoryImpl
        extends BaseRepositoryImpl<RoleModel, RoleEntity, Long>
        implements RoleRepository {

    RoleMapperRepository mapper;
    MongoRoleRepository repository;

    public RoleRepositoryImpl(
            MongoTemplate template,
            RoleMapperRepository mapper,
            MongoRoleRepository repository
    ) {
        super(template);
        this.mapper = mapper;
        this.repository = repository;
    }

    @Override
    protected Class<RoleEntity> entityClass() {
        return RoleEntity.class;
    }

    @Override
    protected RoleEntity toEntity(RoleModel model) {
        return mapper.toEntity(model);
    }

    @Override
    protected RoleModel toModel(RoleEntity entity) {
        return mapper.toModel(entity);
    }

    @Override
    public RoleModel save(RoleModel role) {
        return mapper.toModel(
                repository.save(mapper.toEntity(role))
        );
    }

    @Override
    public RoleModel insert(RoleModel role) {
        return mapper.toModel(
                repository.insert(mapper.toEntity(role))
        );
    }

    @Override
    public Optional<RoleModel> findById(Long id) {
        return repository.findById(id)
                .map(mapper::toModel);
    }

    @Override
    public boolean existsById(Long id) {
        return super.existsById(id);
    }

    @Override
    public int deleteById(Long id) {
        return super.deleteById(id);
    }

    @Override
    public Page<RoleModel> findAll(RoleFilter filter, Pageable pageable) {
        Query query = new Query();

        // Aplica automaticamente os filtros de id, createdAt e updatedAt
        applyBaseFilter(query, filter);

        // Aplica os filtros específicos de Role usando o padrão QueryUtils do projeto
        QueryUtils.addLike(query, "name", filter.getName());
        QueryUtils.addLike(query, "description", filter.getDescription());
        QueryUtils.addEquals(query, "active", filter.getActive());

        // Faz o count, injeta o pageable, busca no banco e mapeia para Model automaticamente
        return toPage(query, pageable);
    }
}