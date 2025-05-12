package ru.thuggeelya.useraccounts.dao.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.proxy.HibernateProxy;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Objects;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user_balance_history")
public class UserBalanceHistory {

    @Id
    @Column(name = "user_id", nullable = false)
    private Long id;

    @MapsId
    @JsonIgnore
    @ToString.Exclude
    @OnDelete(action = OnDeleteAction.CASCADE)
    @OneToOne(optional = false, targetEntity = User.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false, insertable = false, updatable = false)
    private User user;

    @NotNull
    @ColumnDefault("0")
    @Column(name = "initial_balance", nullable = false)
    private BigDecimal initialBalance;

    @Column(name = "current_balance")
    private BigDecimal currentBalance;

    @Column(name = "max_balance")
    private BigDecimal maxBalance;

    @NotNull
    @ColumnDefault("now()")
    @Column(name = "created", nullable = false)
    private Timestamp created;

    @Column(name = "last_updated")
    private Timestamp lastUpdated;

    @NotNull
    @ColumnDefault("true")
    @Column(name = "increment", nullable = false)
    private Boolean increment = false;

    @Override
    public final boolean equals(final Object o) {

        if (this == o) return true;
        if (o == null) return false;

        final Class<?> oEffectiveClass = o instanceof HibernateProxy
                ? ((HibernateProxy)o).getHibernateLazyInitializer().getPersistentClass()
                : o.getClass();
        final Class<?> thisEffectiveClass = this instanceof HibernateProxy
                ? ((HibernateProxy)this).getHibernateLazyInitializer().getPersistentClass()
                : this.getClass();

        if (thisEffectiveClass != oEffectiveClass) return false;

        final UserBalanceHistory userBalanceHistory = (UserBalanceHistory)o;
        return getId() != null && Objects.equals(getId(), userBalanceHistory.getId());
    }

    @Override
    public final int hashCode() {

        return this instanceof HibernateProxy
                ? ((HibernateProxy)this).getHibernateLazyInitializer().getPersistentClass().hashCode()
                : getClass().hashCode();
    }
}