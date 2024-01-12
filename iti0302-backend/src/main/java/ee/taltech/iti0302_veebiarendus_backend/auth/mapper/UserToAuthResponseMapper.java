package ee.taltech.iti0302_veebiarendus_backend.auth.mapper;

import ee.taltech.iti0302_veebiarendus_backend.auth.dto.AuthenticationResponse;
import ee.taltech.iti0302_veebiarendus_backend.user.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface UserToAuthResponseMapper {
    AuthenticationResponse userToAuthResponse(User user, String jwt);
}
