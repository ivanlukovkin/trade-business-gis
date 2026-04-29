package org.lkvkn.gistrade.users.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
            String fieldValue = entry.getValue();
            String fieldName = entry.getKey();
            switch (fieldName) {
                case "first_name" -> fieldName = "firstName";
                case "last_name" -> fieldName = "lastName";
            }
            if (fieldValue != null && !fieldValue.trim().isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get(fieldName), fieldValue));
            }
        }
        if (!predicates.isEmpty()) {
            query.where(predicates.toArray(new Predicate[0]));
        }
        try {
            return entityManager.createQuery(query).getResultList();
        } catch (Exception exception) {
            return new ArrayList<>();
        }
    }
}
