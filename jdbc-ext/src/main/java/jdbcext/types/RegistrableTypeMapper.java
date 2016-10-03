package jdbcext.types;

public interface RegistrableTypeMapper extends TypeMapper {

    void typeRegistered(TypeRegistry typeRegistry);
}
