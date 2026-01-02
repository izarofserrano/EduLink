package com.edulink.dto.user;

/***
 * Password Reset Request DTO
 */

public class PasswordResetRequest {
    private String newPassword;

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}