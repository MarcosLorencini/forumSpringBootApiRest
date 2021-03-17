package br.com.lorencini.forum.config.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@EnableWebSecurity//habilita o modulo seguranca na aplicacao
@Configuration// habilitar fazer algumas configuracoes como beans
public class SecurityConfigurations extends WebSecurityConfigurerAdapter {
	
	@Autowired
	private AutenticacaoService autenticacaoService;
	
	
	//sobrescrever este método para injetar no controller AutenticacaoController
	@Override
	@Bean//devolve um AuthenticationManager e injeta no nosso controller
	protected AuthenticationManager authenticationManager() throws Exception {
		return super.authenticationManager();
	}	
	
	//aqui vai ser configurado quem pode acessar as nossa apis
	//se deixar a classe em branco o spring já não vai deixar acessar as apis 401 já está tudo bloqueado
	//pois ao habilitar o Spring Security os endpoints tornam restritos
	
	//3 metodos do WebSecurityConfigurerAdapter
	
	//configuracoes de autenticacao
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(autenticacaoService).//informa a classe que implementa a logica de autenticacao
		passwordEncoder(new BCryptPasswordEncoder());//informa o algoritimo de hash que vai ser usado na senha
	}
	
	
	//configuracoes de Autrorizacao ex: url, perfil de acesso
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		//tem que indicar o HttpMethod.GET caso contrario todos vão ser permitidos
		http.authorizeRequests()
		.antMatchers(HttpMethod.GET,"/topicos").permitAll()//libera url GET com /topicos
		.antMatchers(HttpMethod.GET,"/topicos/*").permitAll()//libera url GET com /topicos/e qualquer coisa que vier depois
		.antMatchers(HttpMethod.POST,"/auth").permitAll()//liberar url de login
		.anyRequest().authenticated()//qualquer outra requisicao tem que estar autenticada
		.and().csrf().disable()//como a autent é via jsonwebtoken. tem que disable no csrf() tipo ataque de hacker em app via web o spring security vai tentar validar do token do csrf()
		.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);//informa ao spring que quando realizar a atuenticacao não se cria sessão, pois vamos usar token
		//and().formLogin();//gerar um formulario de autenticacao porém ele cria sessão
		
	}
	
	
	//configuracoes de recurso staticos(js, css, img, etc.)
	@Override
	public void configure(WebSecurity web) throws Exception {
	
	}
	
	//gerou o hash 123456 e colocou no arquivo data.sql
	/*
	 * public static void main(String[] args) { System.out.println(new
	 * BCryptPasswordEncoder().encode("123456")); }
	 */
	
	

}
