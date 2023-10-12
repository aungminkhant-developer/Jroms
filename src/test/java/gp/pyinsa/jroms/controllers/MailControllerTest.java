package gp.pyinsa.jroms.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.Date;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import gp.pyinsa.jroms.constants.EmailRole;
import gp.pyinsa.jroms.constants.Status;
import gp.pyinsa.jroms.exceptions.ResourceAlreadyExistsException;
import gp.pyinsa.jroms.models.AppUserDetails;
import gp.pyinsa.jroms.models.EmailTemplate;
import gp.pyinsa.jroms.models.EmailType;
import gp.pyinsa.jroms.models.Role;
import gp.pyinsa.jroms.models.User;
import gp.pyinsa.jroms.services.EmailTemplateService;
import gp.pyinsa.jroms.services.EmailTypeService;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
public class MailControllerTest {

    @MockBean
    private EmailTemplateService emailTemplateService;

    @MockBean
    private EmailTypeService emailTypeService;

    @Autowired
    private MockMvc mockMvc;

    private static String baseUrl;

    private static AppUserDetails userDetails;

    @BeforeAll
    static void setup() {
        baseUrl = "/mng/mail";
        Role role = new Role((short) 1, "ROLE_ADMIN");
        User user = new User("U001", "John", "john", "john@gmail.com", "john", true, Status.ACTIVE, role, new Date(),
                new Date());
        userDetails = new AppUserDetails(user);
    }

    @Test
    void emailTemplatePage_WhenCalled_ShouldReturnPage() throws Exception {
        this.mockMvc.perform(get(baseUrl + "/create").with(user(userDetails)))
                .andExpect(status().is(200))
                .andExpect(model().attributeExists("emailType"))
                .andExpect(model().attributeExists("emailTemplate"))
                .andExpect(model().attributeExists("emailTypeList"))
                .andExpect(model().attributeExists("emailTemplateBtn"))
                .andExpect(view().name("emailTemplate"));
    }

    @Test
    void addEmailType_WhenDataIsValid_ShouldSaveAndRedirect() throws Exception {
        EmailType emailType = new EmailType((short) 1, "Job Offering", Status.ACTIVE, new EmailTemplate(),EmailRole.OTHERS);
        short emailRole=1;

        this.mockMvc.perform(post(baseUrl + "/addEmailType").with(user(userDetails)).with(csrf())
                .param("emailRole", String.valueOf(emailRole))
                .flashAttr(
                "emailType", emailType))
                .andExpect(status().is(302))
                .andExpect(redirectedUrl("/mng/mail/create"));
    }

    private static Stream<Arguments> faultyEmailType() {
        EmailType emailType1 = new EmailType((short) 1, "", Status.ACTIVE, new EmailTemplate(),EmailRole.JOB_OFFER);
        EmailType emailType2 = new EmailType((short) 2, null, Status.ACTIVE, new EmailTemplate(),EmailRole.JOB_OFFER);

        return Stream.of(
                Arguments.of(emailType1),
                Arguments.of(emailType2));
    }

    @ParameterizedTest
    @MethodSource("faultyEmailType")
    void addNewEmailType_WhenDataHasError_ShouldGoBackToViewPage(EmailType emailType) throws Exception {
        short emailRole=1;
        this.mockMvc.perform(post(baseUrl + "/addEmailType").with(user(userDetails)).with(csrf())
                .param("emailRole", String.valueOf(emailRole))
                .flashAttr(
                "emailType", emailType))
                .andExpect(status().is(200))
                .andExpect(model().hasErrors())
                .andExpect(view().name("emailTemplate"));
    }

    @Test
    void addNewEmailType_WhenServerErrorHas_ShouldGoBackToViewPage() throws Exception {
        EmailType emailType = new EmailType((short) 1, "Job Offer", Status.ACTIVE, new EmailTemplate(),EmailRole.OTHERS);
        short emailRole=1;
        Mockito.doThrow(new ResourceAlreadyExistsException("")).when(emailTypeService).saveEmailType(emailType);

        emailType.setName(null);
        this.mockMvc.perform(post(baseUrl + "/addEmailType")
                .with(user(userDetails))
                .with(csrf())
                .param("emailRole", String.valueOf(emailRole))
                .flashAttr(
                        "emailType", emailType))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(view().name("emailTemplate"));
    }

    @Test
    void deleteEmailType_WhenCalled_ShouldDeleteAndRedirect() throws Exception {
        short emailTypeId = 1;

        this.mockMvc
                .perform(post(baseUrl + "/deleteEmailType")
                        .with(user(userDetails))
                        .with(csrf())
                        .param("id", String.valueOf(emailTypeId)))
                .andExpect(status().is(302))
                .andExpect(redirectedUrl("/mng/mail/create"));

        verify(emailTypeService, times(1)).deleteEmailTypeByStatus(emailTypeId);
    }

    @Test
    void addEmailTemplate_WhenDataIsValid_ShouldCreateAndUpdate() throws Exception {
        EmailType emailType = new EmailType((short) 1, "Name", Status.ACTIVE, new EmailTemplate(),EmailRole.OTHERS);
        EmailTemplate emailTemplate = new EmailTemplate((short) 2, "Mail", "Hi hi", Status.ACTIVE, emailType);
        short emailTypeId = 1;

        when(emailTypeService.getEmailType(emailTypeId)).thenReturn(new EmailType());
        when(emailTemplateService.saveEmailTemplate(any(EmailTemplate.class))).thenReturn(emailTemplate);

        this.mockMvc.perform(MockMvcRequestBuilders.post(baseUrl + "/addEmailTemplate")
                .with(user(userDetails))
                .param("emailTypeId", String.valueOf(emailTypeId))
                .with(csrf())
                .flashAttr("emailTemplate", emailTemplate))
                .andExpect(status().is(200))
                .andExpect(MockMvcResultMatchers.view().name("emailTemplate"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("emailTemplateBtn", "addTemplateSuccess"));

        verify(emailTypeService, times(1)).getEmailType(emailTypeId);
        verify(emailTemplateService, times(1)).saveEmailTemplate(any(EmailTemplate.class));
    }

    @Test
    void addEmailTemplate_WhenServerHasError_ShouldGoBackToViewPage() throws Exception {
        EmailTemplate emailTemplate = new EmailTemplate((short) 1, "Subject", "hihih", Status.ACTIVE, new EmailType());
        Mockito.doThrow(new ResourceAlreadyExistsException("")).when(emailTemplateService)
                .saveEmailTemplate(emailTemplate);
        short emailTypeId = 1;
        this.mockMvc
                .perform(post(baseUrl + "/addEmailTemplate")
                        .with(user(userDetails))
                        .param("emailTypeId", String.valueOf(emailTypeId))
                        .with(csrf())
                        .flashAttr("emailTemplate", emailTemplate))
                .andExpect(status().isOk())
                .andExpect(view().name("emailTemplate"));
    }

    @Test
    void editEmailTemplate_WhenDataIsValid_ShouldUpdateAndRedirect() throws Exception {
        EmailType emailType = new EmailType((short) 1, "Type", Status.ACTIVE, new EmailTemplate(),EmailRole.JOB_OFFER);
        EmailTemplate emailTemplate = new EmailTemplate((short) 2, "Template", "Hello", Status.ACTIVE, emailType);
        short emailTypeId = 1;

        mockMvc.perform(MockMvcRequestBuilders.post(baseUrl + "/editEmailTemplate")
                .with(user(userDetails))
                .param("emailTypeId", String.valueOf(emailTypeId))
                .with(csrf())
                .flashAttr("emailTemplate", emailTemplate))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("emailTemplate"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("emailTemplateBtn", "addTemplateSuccess"));

        verify(emailTemplateService, times(1)).editEmailTemplate(emailTemplate);
    }

    @Test
    public void testGetEmailType() throws Exception {
        short typeId = 1;

        // Create a mock EmailType and its associated EmailTemplate
        EmailTemplate emailTemplate = new EmailTemplate((short) 1, "Subject", "Body Text", Status.ACTIVE, new EmailType());
        EmailType emailType = new EmailType(typeId, "Type Name", Status.ACTIVE, emailTemplate,EmailRole.OTHERS);
        when(emailTypeService.getEmailType(typeId)).thenReturn(emailType);

        // Perform the request and assertions
        mockMvc.perform(post(baseUrl+"/getEmailType")
            .with(user(userDetails))
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(String.valueOf(typeId)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(String.valueOf(emailTemplate.getId())))
            .andExpect(jsonPath("$.subject").value(emailTemplate.getSubject()))
            .andExpect(jsonPath("$.bodyText").value(emailTemplate.getBodyText()))
            .andExpect(jsonPath("$.emailTypeName").value(emailType.getName()));

        verify(emailTypeService, times(1)).getEmailType(typeId);
    }

}
