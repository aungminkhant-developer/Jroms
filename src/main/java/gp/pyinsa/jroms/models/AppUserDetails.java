package gp.pyinsa.jroms.models;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import gp.pyinsa.jroms.constants.Status;

@SuppressWarnings("serial")
public class AppUserDetails implements UserDetails {

    private User user;

    public AppUserDetails(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(user.getRole().getName()));
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }
    
    public User getUser() {
        return user;
    }

    public String getId() {
        return user.getId();
    }

    public String getFullName() {
        return user.getName();
    }

    public void setFullName(String fullName) {
        user.setName(fullName);
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return user.getEnabled() && user.getStatus().equals(Status.ACTIVE);
    }

}
