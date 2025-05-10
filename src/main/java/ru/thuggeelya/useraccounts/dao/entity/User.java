package ru.thuggeelya.useraccounts.dao.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "\"user\"")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 500)
    private String name;

    @Column(nullable = false)
    @DateTimeFormat(pattern = "dd.MM.yyyy")
    private LocalDate dateOfBirth;

    @Column(nullable = false, length = 500)
    private String password;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Account account;

    @ToString.Exclude
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<EmailData> emailData = new LinkedList<>();

    @ToString.Exclude
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<PhoneData> phoneData = new LinkedList<>();

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

        final User user = (User)o;
        return getId() != null && Objects.equals(getId(), user.getId());
    }

    @Override
    public final int hashCode() {

        return this instanceof HibernateProxy
                ? ((HibernateProxy)this).getHibernateLazyInitializer().getPersistentClass().hashCode()
                : getClass().hashCode();
    }
}
