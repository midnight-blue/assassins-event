package de.midnightblue.assassin.query;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;

public class UserCircleInfoSerializer extends JsonSerializer<UserCircleInfo> {

  @Override
  public void serialize(
      UserCircleInfo dto, JsonGenerator gen, SerializerProvider serializerProvider)
      throws IOException {
    gen.writeStartObject();
    gen.writeStringField("circleName", dto.circle().getName());
    gen.writeStringField("eliminatedBy", dto.eliminatedBy().orElse("null"));
    gen.writeStringField("roundWonBy", dto.roundWonBy().orElse(""));
    gen.writeStringField("status", dto.status().toString());

    gen.writeEndObject();
  }
}
