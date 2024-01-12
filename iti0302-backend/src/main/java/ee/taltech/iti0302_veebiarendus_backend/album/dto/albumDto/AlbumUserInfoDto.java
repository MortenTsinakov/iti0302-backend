package ee.taltech.iti0302_veebiarendus_backend.album.dto.albumDto;

public record AlbumUserInfoDto(
   boolean like,
   boolean listenLater,
   Integer rating,
   String review
) {}
