package ru.thuggeelya.useraccounts.service.search;

import co.elastic.clients.elasticsearch._types.query_dsl.MatchQuery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.stereotype.Service;
import ru.thuggeelya.useraccounts.dao.repository.search.UserSearchRepository;
import ru.thuggeelya.useraccounts.mapper.UserMapper;
import ru.thuggeelya.useraccounts.model.dto.UserDto;
import ru.thuggeelya.useraccounts.model.search.ElasticSearchUser;
import ru.thuggeelya.useraccounts.model.search.SearchUser;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders.match;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserElasticSearchService implements UserSearchService {

    private final UserMapper userMapper;
    private final UserSearchRepository userSearchRepository;
    private final ElasticsearchOperations elasticsearchOperations;

    @Override
    public void indexUser(final SearchUser user) {

        if (user instanceof ElasticSearchUser elasticUser) {

            log.info("[Elastic] indexing user: {}", elasticUser);

            userSearchRepository.save(elasticUser);
        } else {
            throw new IllegalArgumentException("[Elastic] user must implement ElasticSearchUser");
        }
    }

    @Override
    public void setBalance(final Long id, final BigDecimal balance) {

        final MatchQuery.Builder queryBuilder = match().field("id").query(id.toString());

        final NativeQuery searchQuery = new NativeQueryBuilder().withQuery(queryBuilder.build()._toQuery()).build();

        final SearchHits<ElasticSearchUser> searchHits = elasticsearchOperations.search(
                searchQuery, ElasticSearchUser.class, IndexCoordinates.of("users")
        );

        searchHits.get().map(SearchHit::getContent).findFirst().ifPresentOrElse(
                u -> {
                    u.setBalance(balance.toString());
                    indexUser(u);
                },
                () -> log.error("[Elastic] user not found with id {}", id)
        );
    }

    @Override
    public Page<UserDto> findByDateOfBirthAfter(final String dateOfBirth, final Pageable pageable) {

        log.info("[Elastic] searching users by dateOfBirth after '{}'", dateOfBirth);

        return userSearchRepository.findByDateOfBirthAfter(LocalDate.parse(dateOfBirth, DateTimeFormatter.ofPattern("dd.MM.yyyy")), pageable).map(userMapper::toUserDto);
    }

    @Override
    public Page<UserDto> findByPhone(final String phone, final Pageable pageable) {

        log.info("[Elastic] searching users by phone '{}'", phone);

        return userSearchRepository.findByPhone(phone, pageable).map(userMapper::toUserDto);
    }

    @Override
    public Page<UserDto> findByEmail(final String email, final Pageable pageable) {

        log.info("[Elastic] searching users by email '{}'", email);

        return userSearchRepository.findByEmail(email, pageable).map(userMapper::toUserDto);
    }

    @Override
    public Page<UserDto> findByNameStarts(final String name, final Pageable pageable) {

        log.info("[Elastic] searching users by name starts with '{}'", name);

        return userSearchRepository.findByNameStartingWith(name, pageable).map(userMapper::toUserDto);

    }
}