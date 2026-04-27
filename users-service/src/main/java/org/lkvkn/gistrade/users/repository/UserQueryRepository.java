package org.lkvkn.gistrade.users.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.lkvkn.gistrade.common.enums.AppRole;
import org.lkvkn.gistrade.users.model.User;
import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;

import jakarta.persistence.criteria.Root;

@Repository
public class UserQueryRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public List<User> findByProperties(Map<String, String> properties) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<User> query = criteriaBuilder.createQuery(User.class);
        Root<User> root = query.from(User.class);
        List<Predicate> predicates = new ArrayList<>();
        for (Map.Entry<String, String> entry : properties.entrySet()) {
            String fieldName = entry.getKey();
            String fieldValue = entry.getValue();
            if (fieldValue != null && !fieldValue.trim().isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get(fieldName), fieldValue));
            }
        }
        query.where(new Predicate[0]);
        try {
            return entityManager.createQuery(query).getResultList();
        } catch (Exception _) {
            return null;
        }
    }

    public User partialUpdate(Long userId, Map<String, String> properties) {
        var criteriaBuilder = entityManager.getCriteriaBuilder();
        var update = criteriaBuilder.createCriteriaUpdate(User.class);
        var root = update.from(User.class);
        for (Map.Entry<String, String> entry : properties.entrySet()) {
            String fieldName = entry.getKey();
            String fieldValue = entry.getValue();
            if (fieldValue != null && !fieldValue.trim().isEmpty()) {
                update.set(root.get(fieldName), convertValue(fieldName, fieldValue));
            }
        }
        update.where(criteriaBuilder.equal(root.get("user_id"), userId));
        int updatedCount = entityManager.createQuery(update).executeUpdate();
        if (updatedCount == 0) {
            throw new RuntimeException("User not found with id: " + userId);
        }
        return entityManager.find(User.class, userId);
    }

    private Object convertValue(String fieldName, String fieldValue) {
        if ("role".equals(fieldName)) {
            return AppRole.valueOf(fieldValue.toUpperCase());
        }
        if ("user_id".equals(fieldValue)) {
            return Long.valueOf(fieldValue);
        }
        return fieldValue;
    }
}
