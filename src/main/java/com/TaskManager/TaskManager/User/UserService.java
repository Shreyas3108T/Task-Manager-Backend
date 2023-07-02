package com.TaskManager.TaskManager.User;

import com.TaskManager.TaskManager.Response.Response;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;

import javax.swing.text.html.Option;
import java.sql.Date;
import java.text.ParseException;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    public Optional<Users> findUserByEmail(String email) {
        Optional<Users> UserInfo =  userRepository.findUserByEmail(email);
        return UserInfo;
    }
    public Optional<Users> findUserById(Long id){
        Optional<Users> UserInfo = userRepository.findById(id);
        return UserInfo;
    }

    public Response<?> CreateNewUser(Users users) throws JOSEException {
        Optional<Users> usersOptional = findUserByEmail(users.getEmail());
        if(usersOptional.isPresent()){
            return new Response<>(HttpStatus.ALREADY_REPORTED.value(),false,"User with this email already present","already present");
        }
        System.out.println(users.getPassword());
        String HashedPassword = BCrypt.hashpw(users.getPassword(),BCrypt.gensalt());
        System.out.println(HashedPassword);
        users.setPassword(HashedPassword);
        System.out.println(users.getPassword());
        String Token = GenerateJWT(users.getEmail());
        userRepository.save(users);
        return new Response<>(200,true,"User Account created successfully", Map.of("UserId",users.getId().toString(),"Token",Token));
    }

    public boolean checkPassword(Users user,String password){
        return  BCrypt.checkpw(password, user.getPassword());

    }
    public String GenerateJWT(String body) throws JOSEException {
        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder().subject(body).expirationTime(Date.from(Instant.now().plus(Duration.ofHours(1)))).build();
        SignedJWT singedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256),claimsSet);
        singedJWT.sign(new MACSigner("sULe2VBvR0PvjtUMHcMJaF2F2WE4OmfgsC5pK73qXe0="));
        String jwtToken = singedJWT.serialize();
        return jwtToken;
    }
    public String verifyJWT(String Token) throws ParseException, JOSEException {
        if(Token == null){
            return "No Token";
        }
        String jwt = Token.substring(7);

        SignedJWT signedJWT = SignedJWT.parse(jwt);
        if(signedJWT.verify(new MACVerifier(("sULe2VBvR0PvjtUMHcMJaF2F2WE4OmfgsC5pK73qXe0=")))){
            JWTClaimsSet claimsSet = signedJWT.getJWTClaimsSet();
            String subject = claimsSet.getSubject();
            return subject;
        }
        else{
            return "null";
        }
    }


    public List<Users> AllUsers(){
        List<Users> users = userRepository.findAll();
        return users;
    }


    public Response<?> LoginUser(String email, String password) throws JOSEException {
        Optional<Users> User = findUserByEmail(email);
        if(User.isEmpty()){
            return new Response<>(HttpStatus.NOT_FOUND.value(), false,"No user with email " +email +" found","no email");
        }
        Users user = User.get();
        if(!checkPassword(user,password)){
            return new Response<>(HttpStatus.UNAUTHORIZED.value(),false,"Wrong Password","incorrect password");
        }
        String Token = GenerateJWT(user.getEmail());
        return new Response<>(200,true,"successful login",Map.of("Token",Token,"name",user.getName()));
    }
}
