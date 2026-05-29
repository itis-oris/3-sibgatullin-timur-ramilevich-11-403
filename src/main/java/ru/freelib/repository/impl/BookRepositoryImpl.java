package ru.freelib.repository.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import ru.freelib.model.entity.Author;
import ru.freelib.model.entity.Book;
import ru.freelib.model.entity.Genre;
import ru.freelib.repository.BookRepositoryCustom;

import java.util.ArrayList;
import java.util.List;

@Repository
public class BookRepositoryImpl implements BookRepositoryCustom {
    @PersistenceContext
    private EntityManager em;

    @Override
    public Page<Book> findByDynamicFilters(String titleFragment, String authorName, List<Long> genreIds,
                                           Long minViews, Long maxViews, Pageable pageable) {
        CriteriaBuilder cb = em.getCriteriaBuilder();

        CriteriaQuery<Book> cq = cb.createQuery(Book.class);
        Root<Book> book = cq.from(Book.class);
        book.fetch("author", JoinType.LEFT);
        book.fetch("genres", JoinType.LEFT);

        List<Predicate> predicates = buildPredicates(cb, book, titleFragment, authorName, genreIds, minViews, maxViews);
        cq.where(predicates.toArray(new Predicate[0]));
        cq.distinct(true);

        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Book> countRoot = countQuery.from(Book.class);
        List<Predicate> countPredicates = buildPredicates(cb, countRoot, titleFragment, authorName, genreIds, minViews, maxViews);
        countQuery.select(cb.countDistinct(countRoot)).where(countPredicates.toArray(new Predicate[0]));
        Long total = em.createQuery(countQuery).getSingleResult();

        TypedQuery<Book> typedQuery = em.createQuery(cq);
        typedQuery.setFirstResult((int) pageable.getOffset());
        typedQuery.setMaxResults(pageable.getPageSize());

        return new PageImpl<>(typedQuery.getResultList(), pageable, total);
    }

    private List<Predicate> buildPredicates(CriteriaBuilder cb, Root<Book> book,
                                            String titleFragment, String authorName,
                                            List<Long> genreIds, Long minViews, Long maxViews) {
        List<Predicate> predicates = new ArrayList<>();

        if (titleFragment != null && !titleFragment.isBlank()) {
            predicates.add(cb.like(cb.lower(book.get("title")), "%" + titleFragment.toLowerCase() + "%"));
        }
        if (authorName != null && !authorName.isBlank()) {
            Join<Book, Author> authorJoin = book.join("author", JoinType.INNER);
            predicates.add(cb.like(cb.lower(authorJoin.get("nickname")), "%" + authorName.toLowerCase() + "%"));
        }
        if (genreIds != null && !genreIds.isEmpty()) {
            Join<Book, Genre> genreJoin = book.join("genres", JoinType.INNER);
            predicates.add(genreJoin.get("id").in(genreIds));
        }
        if (minViews != null) {
            predicates.add(cb.greaterThanOrEqualTo(book.get("views"), minViews));
        }
        if (maxViews != null) {
            predicates.add(cb.lessThanOrEqualTo(book.get("views"), maxViews));
        }

        return predicates;
    }
}