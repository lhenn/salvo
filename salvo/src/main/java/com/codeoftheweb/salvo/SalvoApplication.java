package com.codeoftheweb.salvo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@SpringBootApplication
public class SalvoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SalvoApplication.class, args);
	}
	@Bean
	public PasswordEncoder passwordEncoder() {
		return PasswordEncoderFactories.createDelegatingPasswordEncoder();

	}

	@Bean
	public CommandLineRunner initData(PlayerRepository playerrepository, GameRepository gamerepository,
	GamePlayerRepository gameplayerrepository, ShipRepository shiprepository, SalvoRepository salvorepository,
									  ScoreRepository scorerepository, PasswordEncoder passwordEncoder) {

		return (args) -> {

			Player jBauer = new Player("j.bauer@ctu.gov","24");
			Player cOBrian = new Player("c.obrian@ctu.gov","42");
			Player kBauer = new Player("kim_bauer@gmail.com","kb");
			Player tAlmeida = new Player("t.almeida@ctu.gov","mole");

			jBauer.setPassword(passwordEncoder.encode(jBauer.getPassword()));
			cOBrian.setPassword((passwordEncoder.encode(cOBrian.getPassword())));
			kBauer.setPassword(passwordEncoder.encode(kBauer.getPassword()));
			tAlmeida.setPassword(passwordEncoder.encode(tAlmeida.getPassword()));

			playerrepository.save(jBauer);
			playerrepository.save(cOBrian);
			playerrepository.save(kBauer);
			playerrepository.save(tAlmeida);

			long h1 = 1;
			long h2 = 2;
			long h3 = 3;
			long h4 = 4;
			long h5 = 5;
			long h6 = 6;
			long h7 = 7;

			Game game1 = new Game();
			Date date1 = game1.getGameDate();

			Date date2 = Date.from(date1.toInstant().plus(h1, ChronoUnit.HOURS));
			Date date3 = Date.from(date1.toInstant().plus(h2, ChronoUnit.HOURS));
			Date date4 = Date.from(date1.toInstant().plus(h3, ChronoUnit.HOURS));
			Date date5 = Date.from(date1.toInstant().plus(h4, ChronoUnit.HOURS));
			Date date6 = Date.from(date1.toInstant().plus(h5, ChronoUnit.HOURS));
			Date date7 = Date.from(date1.toInstant().plus(h6, ChronoUnit.HOURS));
			Date date8 = Date.from(date1.toInstant().plus(h7, ChronoUnit.HOURS));

			Game game2 = new Game();
			game2.setGameDate(date2);
			Game game3 = new Game();
			game3.setGameDate(date3);
			Game game4 = new Game();
			game4.setGameDate(date4);
			Game game5 = new Game();
			game5.setGameDate(date5);
			Game game6 = new Game();
			game6.setGameDate(date6);
			Game game7 = new Game();
			game7.setGameDate(date7);
			Game game8 = new Game();
			game8.setGameDate(date8);

			gamerepository.save(game1);
			gamerepository.save(game2);
			gamerepository.save(game3);
			gamerepository.save(game4);
			gamerepository.save(game5);
			gamerepository.save(game6);
			gamerepository.save(game7);
			gamerepository.save(game8);

			GamePlayer gameplayer1_1 = new GamePlayer(game1, jBauer);
			GamePlayer gameplayer1_2 = new GamePlayer(game1, cOBrian);
			GamePlayer gameplayer2_1 = new GamePlayer(game2, jBauer);
			GamePlayer gameplayer2_2 = new GamePlayer(game2, cOBrian);
			GamePlayer gameplayer3_1 = new GamePlayer(game3, cOBrian);
			GamePlayer gameplayer3_2 = new GamePlayer(game3, tAlmeida);
			GamePlayer gameplayer4_1 = new GamePlayer(game4, cOBrian);
			GamePlayer gameplayer4_2 = new GamePlayer(game4, jBauer);
			GamePlayer gameplayer5_1 = new GamePlayer(game5, tAlmeida);
			GamePlayer gameplayer5_2 = new GamePlayer(game5, jBauer);
			GamePlayer gameplayer6_1 = new GamePlayer(game6, kBauer);
			GamePlayer gameplayer7_1 = new GamePlayer(game7, tAlmeida);
			GamePlayer gameplayer8_1 = new GamePlayer(game8, kBauer);
			GamePlayer gameplayer8_2 = new GamePlayer(game8, tAlmeida);

			gameplayerrepository.save(gameplayer1_1);
			gameplayerrepository.save(gameplayer1_2);
			gameplayerrepository.save(gameplayer2_1);
			gameplayerrepository.save(gameplayer2_2);
			gameplayerrepository.save(gameplayer3_1);
			gameplayerrepository.save(gameplayer3_2);
			gameplayerrepository.save(gameplayer4_1);
			gameplayerrepository.save(gameplayer4_2);
			gameplayerrepository.save(gameplayer5_1);
			gameplayerrepository.save(gameplayer5_2);
			gameplayerrepository.save(gameplayer6_1);
			gameplayerrepository.save(gameplayer7_1);
			gameplayerrepository.save(gameplayer8_1);
			gameplayerrepository.save(gameplayer8_2);

			List<String> h2h3h4 = Arrays.asList("H2","H3", "H4");
			List<String> e1f1g1 = Arrays.asList("E1","F1", "G1");
			List<String> b4b5 = Arrays.asList("B4", "B5");
			List<String> b5c5d5 = Arrays.asList("B5","C5", "D5");
			List<String> f1f2 = Arrays.asList("F1","F2");
			List<String> c6c7 = Arrays.asList("C6","C7");
			List<String> a2a3a4 = Arrays.asList("A2","A3","A4");
			List<String> g6h6 = Arrays.asList("G6","H6");

			Ship ship1 = new Ship("Destroyer", h2h3h4, gameplayer1_1);
			Ship ship2 = new Ship("Submarine", e1f1g1, gameplayer1_1);
			Ship ship3 = new Ship("Patrol Boat", b4b5, gameplayer1_1);
			Ship ship3_1 = new Ship("Destroyer", b5c5d5, gameplayer1_2);
			Ship ship4 = new Ship("Patrol Boat", f1f2, gameplayer1_2);
			Ship ship5 = new Ship("Destroyer", b5c5d5, gameplayer2_1);
			Ship ship6 = new Ship("Patrol Boat", c6c7, gameplayer2_1);
			Ship ship7 = new Ship("Submarine", a2a3a4, gameplayer2_2);
			Ship ship8 = new Ship("Patrol Boat", g6h6, gameplayer2_2);
			Ship ship9 = new Ship("Destroyer", b5c5d5, gameplayer3_1);
			Ship ship10 = new Ship("Patrol Boat", c6c7, gameplayer3_1);
			Ship ship11 = new Ship("Submarine", a2a3a4, gameplayer3_2);
			Ship ship12 = new Ship("Patrol Boat", g6h6, gameplayer3_2);
			Ship ship13 = new Ship("Destroyer", b5c5d5, gameplayer4_1);
			Ship ship14 = new Ship("Patrol Boat", c6c7, gameplayer4_1);
			Ship ship15 = new Ship("Submarine", a2a3a4, gameplayer4_2);
			Ship ship16 = new Ship("Patrol Boat", g6h6, gameplayer4_2);
			Ship ship17 = new Ship("Destroyer", b5c5d5, gameplayer5_1);
			Ship ship18 = new Ship("Patrol Boat", c6c7, gameplayer5_1);
			Ship ship19 = new Ship("Submarine", a2a3a4, gameplayer5_2);
			Ship ship20 = new Ship("Patrol Boat", g6h6, gameplayer5_2);
			Ship ship21 = new Ship("Destroyer", b5c5d5, gameplayer6_1);
			Ship ship22 = new Ship("Patrol Boat", c6c7, gameplayer6_1);
			Ship ship23 = new Ship("Destroyer", b5c5d5, gameplayer8_1);
			Ship ship24 = new Ship("Patrol Boat", c6c7, gameplayer8_1);
			Ship ship25 = new Ship("Submarine", a2a3a4, gameplayer8_2);
			Ship ship26 = new Ship("Patrol Boat", g6h6, gameplayer8_2);

			shiprepository.save(ship1);
			shiprepository.save(ship2);
			shiprepository.save(ship3);
			shiprepository.save(ship3_1);
			shiprepository.save(ship4);
			shiprepository.save(ship5);
			shiprepository.save(ship6);
			shiprepository.save(ship7);
			shiprepository.save(ship8);
			shiprepository.save(ship9);
			shiprepository.save(ship10);
			shiprepository.save(ship11);
			shiprepository.save(ship12);
			shiprepository.save(ship13);
			shiprepository.save(ship14);
			shiprepository.save(ship15);
			shiprepository.save(ship16);
			shiprepository.save(ship17);
			shiprepository.save(ship18);
			shiprepository.save(ship19);
			shiprepository.save(ship20);
			shiprepository.save(ship21);
			shiprepository.save(ship22);
			shiprepository.save(ship23);
			shiprepository.save(ship24);
			shiprepository.save(ship25);
			shiprepository.save(ship26);

			Salvo salvo1 = new Salvo(1, Arrays.asList("B5","C5","F1"), gameplayer1_1);
			Salvo salvo2 = new Salvo(1, Arrays.asList("B4","B5","B6"), gameplayer1_2);
			Salvo salvo3 = new Salvo(2, Arrays.asList("F2","D5"), gameplayer1_1);
			Salvo salvo4 = new Salvo(2, Arrays.asList("E1", "H3","A2"), gameplayer1_2);
			Salvo salvo5 = new Salvo(1, Arrays.asList("A2","A4","G6"), gameplayer2_1);
			Salvo salvo6 = new Salvo(1, Arrays.asList("B5","D5","C7"), gameplayer2_2);
			Salvo salvo7 = new Salvo(2,Arrays.asList("A3","H6"), gameplayer2_1);
			Salvo salvo8 = new Salvo(2,Arrays.asList("C5","C6"),gameplayer2_2);
			Salvo salvo9 = new Salvo(1,Arrays.asList("G6","H6","A4"),gameplayer3_1);
			Salvo salvo10 = new Salvo(1,Arrays.asList("H1","H2","H3"),gameplayer3_2);
			Salvo salvo11 = new Salvo(2,Arrays.asList("A2","A3","D8"),gameplayer3_1);
			Salvo salvo12 = new Salvo(2,Arrays.asList("E1","F2","G3"),gameplayer3_2);
			Salvo salvo13 = new Salvo(1,Arrays.asList("A3","A4","F7"),gameplayer4_1);
			Salvo salvo14 = new Salvo(1,Arrays.asList("B5","C6","H1"),gameplayer4_2);
			Salvo salvo15 = new Salvo(2,Arrays.asList("A2","G6","H6"),gameplayer4_1);
			Salvo salvo16 = new Salvo(2,Arrays.asList("C5","C7","D5"),gameplayer4_2);
			Salvo salvo17 = new Salvo(1,Arrays.asList("A1","A2","A3"),gameplayer5_1);
			Salvo salvo18 = new Salvo(1,Arrays.asList("B5","B6","C7"),gameplayer5_2);
			Salvo salvo19 = new Salvo(2,Arrays.asList("G6","G7","G8"),gameplayer5_1);
			Salvo salvo20 = new Salvo(2,Arrays.asList("C6","D6","E6"),gameplayer5_2);
			Salvo salvo21 = new Salvo(3,Arrays.asList("H1","H8"),gameplayer5_2);

			salvorepository.save(salvo1);
			salvorepository.save(salvo2);
			salvorepository.save(salvo3);
			salvorepository.save(salvo4);
			salvorepository.save(salvo5);
			salvorepository.save(salvo6);
			salvorepository.save(salvo7);
			salvorepository.save(salvo8);
			salvorepository.save(salvo9);
			salvorepository.save(salvo10);
			salvorepository.save(salvo11);
			salvorepository.save(salvo12);
			salvorepository.save(salvo13);
			salvorepository.save(salvo14);
			salvorepository.save(salvo15);
			salvorepository.save(salvo16);
			salvorepository.save(salvo17);
			salvorepository.save(salvo18);
			salvorepository.save(salvo19);
			salvorepository.save(salvo20);
			salvorepository.save(salvo21);

			Score score1 = new Score(game1, jBauer, 1.0);
			Score score2 = new Score(game1, cOBrian, 0.0);
			Score score3 = new Score(game2, jBauer, 0.5);
			Score score4 = new Score(game2, cOBrian,0.5);
			Score score5 = new Score(game3, cOBrian, 1.0);
			Score score6 = new Score(game3, tAlmeida, 0.0);
			Score score7 = new Score(game4, cOBrian, 0.5);
			Score score8 = new Score(game4, jBauer, 0.5);

			scorerepository.save(score1);
			scorerepository.save(score2);
			scorerepository.save(score3);
			scorerepository.save(score4);
			scorerepository.save(score5);
			scorerepository.save(score6);
			scorerepository.save(score7);
			scorerepository.save(score8);
		};
	}
}

@Configuration
class WebSecurityConfiguration extends GlobalAuthenticationConfigurerAdapter {

	@Autowired
	PlayerRepository playerRepository;

	@Override
	public void init(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(inputName-> {
			Player player = playerRepository.findByUserName(inputName);
			if (player != null) {
				return new User(player.getUserName(), player.getPassword(),
						AuthorityUtils.createAuthorityList("USER"));
			} else {
				throw new UsernameNotFoundException("Unknown user: " + inputName);
			}
		});
	}
}


@Configuration
@EnableWebSecurity
class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Override
	protected void configure(HttpSecurity http) throws Exception {


		http.headers().frameOptions().disable();
		http.authorizeRequests()
//				.antMatchers("/web/**").permitAll()
//				.antMatchers("/api/leaderboard").permitAll()
//				.antMatchers("/api/login").permitAll()
//				.antMatchers("/api/signup").permitAll()
//				.anyRequest().fullyAuthenticated()
				.antMatchers("/**").permitAll()
				.and()
				.formLogin();

		http.formLogin()
				.usernameParameter("userName")
				.passwordParameter("password")
				.loginPage("/api/login");

		http.logout().logoutUrl("/api/logout");

		// turn off checking for CSRF tokens
		http.csrf().disable();

		// if user is not authenticated, just send an authentication failure response
		http.exceptionHandling().authenticationEntryPoint((req, res, exc) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED));

		// if login is successful, just clear the flags asking for authentication
		http.formLogin().successHandler((req, res, auth) -> clearAuthenticationAttributes(req));

		// if login fails, just send an authentication failure response
		http.formLogin().failureHandler((req, res, exc) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED));

		// if logout is successful, just send a success response
		http.logout().logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler());
	}

	private void clearAuthenticationAttributes(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session != null) {
			session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
		}
	}

}
