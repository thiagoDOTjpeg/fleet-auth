package com.fleet.auth_service.domain.model;

import com.fleet.auth_service.domain.enums.UserType;
import jakarta.persistence.*;
import org.jspecify.annotations.NullMarked;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

@Entity(name = "users")
public class User implements UserDetails {
  @Id
  private UUID id;
  @Column(unique = true)
  private String name;
  @Column(unique = true)
  private String email;
  @Column
  private String password;
  @Column(name = "user_type")
  private UserType userType;
  @Column
  private Boolean active;
  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
  private Set<Role> roles;
  @Column(name = "created_at")
  private Date createdAt;
  @Column(name = "updated_at")
  private Date updatedAt;

  public User() {
  }

  public User(UUID id, String name, String email, String password, UserType userType, Boolean active, Set<Role> roles, Date createdAt, Date updatedAt) {
    this.id = id;
    this.name = name;
    this.email = email;
    this.password = password;
    this.userType = userType;
    this.active = active;
    this.roles = roles;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  @Override
  @NullMarked
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return this.roles;
  }

  public String getPassword() {
    return this.password;
  }

  @Override
  @NullMarked
  public String getUsername() {
    return this.name;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public UserType getUserType() {
    return userType;
  }

  public void setUserType(UserType userType) {
    this.userType = userType;
  }

  public Boolean getActive() {
    return active;
  }

  public void setActive(Boolean active) {
    this.active = active;
  }

  public Date getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(Date createdAt) {
    this.createdAt = createdAt;
  }

  public Date getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(Date updatedAt) {
    this.updatedAt = updatedAt;
  }

  public Set<Role> getRoles() {
    return roles;
  }

  public void setRoles(Set<Role> roles) {
    this.roles = roles;
  }
}
