package com.samoilov.dev.account.service.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.samoilov.dev.account.service.model.RoleType;
import com.samoilov.dev.account.service.validation.ValidPassword;
import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user")
public class UserProfileEntity implements UserDetails {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    @GeneratedValue(generator = "id-generator")
    @GenericGenerator(
            name = "id-generator",
            parameters = @Parameter(name = "sequence", value="user_seq"),
            strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator"
    )
    private Long id;

    @Version
    @Column(name = "version")
    private int version;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @ValidPassword
    @Column(name = "password", nullable = false)
    private String password;

    @ElementCollection
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    private List<RoleType> roles;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PaymentEntity> payments;

    @Override
    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        this.roles.forEach(role -> authorities.add(new SimpleGrantedAuthority(role.toString())));
        return authorities;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return Boolean.TRUE;
    }

    @Override
    public boolean isAccountNonExpired() {
        return Boolean.TRUE;
    }

}
