package com.test.automation.dataproviders;

import java.util.List;

import org.testng.annotations.DataProvider;

import com.test.automation.data.User;

/**
 * Usuários para rodar o mesmo teste com cada perfil do Sauce Demo.
 */
public class UserDataProvider {

    /** Todos os usuários que conseguem entrar no sistema (sem o locked_out_user). */
    public static final List<User> LOGIN_USERS = List.of(
            User.STANDARD,
            User.PROBLEM,
            User.PERFORMANCE_GLITCH,
            User.ERROR,
            User.VISUAL);

    private UserDataProvider() {
    }

    @DataProvider(name = "loginUsers")
    public static Object[][] loginUsers() {
        return LOGIN_USERS.stream()
                .map(user -> new Object[] { user })
                .toArray(Object[][]::new);
    }
}