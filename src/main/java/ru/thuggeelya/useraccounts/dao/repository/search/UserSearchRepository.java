package ru.thuggeelya.useraccounts.dao.repository.search;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;
import ru.thuggeelya.useraccounts.model.search.ElasticSearchUser;

import java.time.LocalDate;

import static ru.thuggeelya.useraccounts.util.ElasticsearchQueryUtils.EMAIL_QUERY;
import static ru.thuggeelya.useraccounts.util.ElasticsearchQueryUtils.PHONE_QUERY;

@Repository
public interface UserSearchRepository extends ElasticsearchRepository<ElasticSearchUser, String> {

    @Query(EMAIL_QUERY)
    Page<ElasticSearchUser> findByEmail(final String email, Pageable pageable);

    @Query(PHONE_QUERY)
    Page<ElasticSearchUser> findByPhone(final String phone, Pageable pageable);

    Page<ElasticSearchUser> findByDateOfBirthAfter(final LocalDate dateOfBirth, Pageable pageable);

    Page<ElasticSearchUser> findByNameStartingWith(final String name, Pageable pageable);
}
