package com.nvsstagemanagement.nvs_stage_management.service.impl;


import com.nvsstagemanagement.nvs_stage_management.model.User;
import com.nvsstagemanagement.nvs_stage_management.service.IEmailService;
import com.nvsstagemanagement.nvs_stage_management.service.IUserService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

@Service
public class EmailService implements IEmailService {
    @Autowired
    private  JavaMailSender mailSender;
    public void sendEmail( User user)  {

        MimeMessage message = mailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom("nvsstatemanagement@gmail.com");
            helper.setTo(user.getEmail());
            helper.setSubject("Email kích hoạt tài khoản NVSHCM");

            // Tạo nội dung HTML với các thẻ và CSS tùy chỉnh
            String htmlContent = "<!DOCTYPE html>\r\n" +
                    "<html>\r\n" +
                    "  <head>\r\n" +
                    "    <style>\r\n" +
                    "      body { font-family: Arial, sans-serif; background-color: #f4f4f4; }\r\n" +
                    "      .email-container {\r\n" +
                    "        max-width: 600px;\r\n" +
                    "        margin: 20px auto;\r\n" +
                    "        background: #ffffff;\r\n" +
                    "        padding: 20px;\r\n" +
                    "        border-radius: 8px;\r\n" +
                    "        box-shadow: 0px 0px 10px rgba(0, 0, 0, 0.1);\r\n" +
                    "        border: 5px solid transparent;\r\n" +
                    "        border-image: linear-gradient(45deg, blue, purple) 1;\r\n" +
                    "      }\r\n" +
                    "      .email-header { text-align: center; font-size: 24px; font-weight: bold; color: #4a00e0; }\r\n" +
                    "      .email-content { text-align: center; font-size: 16px; color: #333; }\r\n" +
                    "      .email-content b { color: #000; }\r\n" +
                    "      .activation-button {\r\n" +
                    "        display: inline-block;\r\n" +
                    "        margin: 20px auto;\r\n" +
                    "        padding: 10px 20px;\r\n" +
                    "        background: #007bff;\r\n" +
                    "        color: #ffffff;\r\n" +
                    "        text-decoration: none;\r\n" +
                    "        border-radius: 5px;\r\n" +
                    "      }\r\n" +
                    "      .email-footer {\r\n" +
                    "        margin-top: 20px;\r\n" +
                    "        text-align: center;\r\n" +
                    "        font-size: 12px;\r\n" +
                    "        color: #666;\r\n" +
                    "      }\r\n" +
                    "    </style>\r\n" +
                    "  </head>\r\n" +
                    "  <body>\r\n" +
                    "    <div class='email-container'>\r\n" +
                    "      <div class='email-header'>NVS State Management</div>\r\n" +
                    "      <div class='email-content'>\r\n" +
                    "        <p>Email kích hoạt</p>\r\n" +
                    "        <p>Xin chào <b>" + user.getFullName() + "</b></p>\r\n" +
                    "        <p>Tên đăng nhập của bạn là: <b>" + user.getEmail() + "</b></p>\r\n" +
                    "        <p>Chào mừng bạn đã đăng kí NVS State Management.</p>\r\n" +
                    "        <p>Đây là Email kích hoạt tài khoản. Bấm vào nút bên dưới để kích hoạt tài khoản.</p>\r\n" +
                    "        <a class='activation-button' href='" + "activationLink" + "'>Kích hoạt tài khoản</a>\r\n" +
                    "      </div>\r\n" +
                    "      <div class='email-footer'>\r\n" +
                    "        <p>Email sent by Admin NVS State Management</p>\r\n" +
                    "        <p><a href='mailto:nvsstatemanagement@gmail.com'>nvsstatemanagement@gmail.com</a> | +848386838668</p>\r\n" +
                    "      </div>\r\n" +
                    "    </div>\r\n" +
                    "  </body>\r\n" +
                    "</html>";

            helper.setText(htmlContent, true);

            mailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    // this function for testing
    public void sendEmail2( String email)  {

//         SimpleMailMessage message = new SimpleMailMessage();
//         message.setFrom("hoangtuan221001@gmail.com");
//         message.setTo(email);
//         message.setText(body);
//         message.setSubject(subject);
//         mailSender.send(message);
        MimeMessage message = mailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom("nvsstatemanagement@gmail.com");
            helper.setTo(email);
            helper.setSubject("Email kích hoạt tài khoản NVSHCM");

            // Tạo nội dung HTML với các thẻ và CSS tùy chỉnh
            String htmlContent = "<!DOCTYPE html>\r\n" +
                    "<html>\r\n" +
                    "  <head>\r\n" +
                    "    <style>\r\n" +
                    "      body { font-family: Arial, sans-serif; background-color: #f4f4f4; }\r\n" +
                    "      .email-container {\r\n" +
                    "        max-width: 600px;\r\n" +
                    "        margin: 20px auto;\r\n" +
                    "        background: #ffffff;\r\n" +
                    "        padding: 20px;\r\n" +
                    "        border-radius: 8px;\r\n" +
                    "        box-shadow: 0px 0px 10px rgba(0, 0, 0, 0.1);\r\n" +
                    "        border: 5px solid transparent;\r\n" +
                    "        border-image: linear-gradient(45deg, blue, purple) 1;\r\n" +
                    "      }\r\n" +
                    "      .email-header { text-align: center; font-size: 24px; font-weight: bold; color: #4a00e0; }\r\n" +
                    "      .email-content { text-align: center; font-size: 16px; color: #333; }\r\n" +
                    "      .email-content b { color: #000; }\r\n" +
                    "      .activation-button {\r\n" +
                    "        display: inline-block;\r\n" +
                    "        margin: 20px auto;\r\n" +
                    "        padding: 10px 20px;\r\n" +
                    "        background: #007bff;\r\n" +
                    "        color: #ffffff;\r\n" +
                    "        text-decoration: none;\r\n" +
                    "        border-radius: 5px;\r\n" +
                    "      }\r\n" +
                    "      .email-footer {\r\n" +
                    "        margin-top: 20px;\r\n" +
                    "        text-align: center;\r\n" +
                    "        font-size: 12px;\r\n" +
                    "        color: #666;\r\n" +
                    "      }\r\n" +
                    "    </style>\r\n" +
                    "  </head>\r\n" +
                    "  <body>\r\n" +
                    "    <div class='email-container'>\r\n" +
                    "      <div class='email-header'>NVSStateManagement</div>\r\n" +
                    "      <div class='email-content'>\r\n" +
                    "        <p>Email kích hoạt</p>\r\n" +
                    "        <p>Xin chào <b>" + "user.getFullName()" + "</b></p>\r\n" +
                    "        <p>Tên đăng nhập của bạn là: <b>" + "user.getEmail()" + "</b></p>\r\n" +
                    "        <p>Chào mừng bạn đã đăng kí NVS State Management.</p>\r\n" +
                    "        <p>Đây là Email kích hoạt tài khoản. Bấm vào nút bên dưới để kích hoạt tài khoản.</p>\r\n" +
                    "        <a class='activation-button' href='" + "activationLink" + "'>Kích hoạt tài khoản</a>\r\n" +
                    "      </div>\r\n" +
                    "      <div class='email-footer'>\r\n" +
                    "        <p>Email sent by LinkNeverDie</p>\r\n" +
                    "        <p><a href='mailto:nvsstatemanagement@gmail.com'>nvsstatemanagement@gmail.com</a> | +848386838668</p>\r\n" +
                    "      </div>\r\n" +
                    "    </div>\r\n" +
                    "  </body>\r\n" +
                    "</html>";

            helper.setText(htmlContent, true);

            mailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        System.out.println("Email đã được gửi thành công!");
    }
}
