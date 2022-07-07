package com.iftm.client.tests.resources;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iftm.client.dto.ClientDTO;
import com.iftm.client.entities.Client;
import com.iftm.client.services.ClientService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
public class ClientResourcesTests {

    @Autowired
    private MockMvc mockMvc;    
    // Para o teste real da aplicação iremos comentar ou retirar.
    //@MockBean
    //private ClientService service;
    private int qtdClient = 12;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testarListarTodosClientesRetornaOKeClientes() throws Exception{
        // Configuração do Mock
        /*
        List<ClientDTO> listaClientes = new ArrayList<ClientDTO>(); 
        listaClientes.add(new ClientDTO(new Client(7l, "Jose Saramago", "10239254871", 5000.0, Instant.parse("1996-12-23T07:00:00Z"), 0)));

        listaClientes.add(new ClientDTO(new Client(4l, "Carolina Maria de Jesus", "10419244771", 7500.0, Instant.parse("1996-12-23T07:00:00Z"), 0)));

        listaClientes.add(new ClientDTO(new Client(8l, "Toni Morrison", "10219344681", 10000.0, Instant.parse("1940-02-23T07:00:00Z"), 0)));

        Page<ClientDTO> page = new PageImpl<>(listaClientes);
        when(service.findAllPaged(any())).thenReturn(page);
        qtdClient = 3;
        */
        // Realizar o teste
        mockMvc.perform(get("/clients/")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").exists())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[?(@.id == '%s')]", 7L).exists())
                .andExpect(jsonPath("$.content[?(@.id == '%s')]", 4L).exists())
                .andExpect(jsonPath("$.content[?(@.id == '%s')]", 8L).exists())
                .andExpect(jsonPath("$.numberOfElements").exists())
                .andExpect(jsonPath("$.totalElements").value(qtdClient))
                .andExpect(jsonPath("$.first").exists())
                .andExpect(jsonPath("$.first").value(true))
                .andExpect(jsonPath("$.totalPages").exists())
                .andExpect(jsonPath("$.totalPages").value(1));
    }

	@Test
	public void testarBuscaPorIDExistenteRetornaJsonCorreto() throws Exception {
		long idExistente = 3L;
		ResultActions resultado = mockMvc.perform(get("/clients/{id}",idExistente)
				.accept(MediaType.APPLICATION_JSON));
		resultado.andExpect(status().isOk());
        resultado.andExpect(jsonPath("$.name").exists());		
		resultado.andExpect(jsonPath("$.name").value("Clarice Lispector"));
		resultado.andExpect(jsonPath("$.id").exists());
		resultado.andExpect(jsonPath("$.id").value(idExistente));
		
	}
	
	@Test
	public void testarBuscaPorIdNaoExistenteRetornaNotFound() throws Exception {
		long idNaoExistente = 300L;
		ResultActions resultado = mockMvc.perform(get("/clients/{id}", idNaoExistente)
				.accept(MediaType.APPLICATION_JSON));
		resultado.andExpect(status().isNotFound());
		resultado.andExpect(jsonPath("$.error").exists());
		resultado.andExpect(jsonPath("$.error").value("Resource not found"));
		resultado.andExpect(jsonPath("$.message").exists());
		resultado.andExpect(jsonPath("$.message").value("Entity not found"));
	}

    /*
		* Parte1: Utilizando o projeto base que estamos fazendo em aula, favor implementar os seguintes
		   testes na classe ClientResourcesTests(opcional : utilizar o mockbean da classe ClientService) :
	*/

    /*
     * insert deveria retornar “created” (código 201), bem como o produto criado, verifique no
        mínimo dois atributos.
        - (Opcional) será necessário programar a simulação do método insert da classe MockBean
        service. Não passe o objeto esperado e sim o método any(), que retorna uma simulação de
        algum objeto qualquer, assim evitamos alguns erros. Caso deseje passar um objeto DTO
        como parametro do método insert, você precisará criar os métodos equals e hash da classe
        DTO.
        - Como criar um Json no java (clientDTO é um objeto já instanciado):
        // Junto com os atributos da classe de teste, faça a injeção de dependência abaixo
        @Autowired
        private ObjectMapper objectMapper;
        // no método de teste insira os códigos de criação de JSON
        String json = objectMapper.writeValueAsString(clientDTO);
        ResultActions result = 

        mockMvc.perform(post("/clients/")
        .content(json)
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON));
     */

     @Test
     public void testarInsertClientRetornaCreated201 () throws Exception {
        Long idExistente = 13L;

        ClientDTO clientDTO = new ClientDTO(13L,"Phabliny Martins", "10619244881", 1500.0, Instant.parse("1995-12-08T09:00:00Z"), (Integer)0);
        String json = objectMapper.writeValueAsString(clientDTO);
            mockMvc.perform(post("/clients/")
                .content(json)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").exists())		
                .andExpect(jsonPath("$.name").value("Phabliny Martins"))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.id").value(idExistente));
     }

     /*
      * delete deveria
        ◦ retornar “no content” (código 204) quando o id existir
        ◦ retornar “not found” (código 404) quando o id não existir
        ◦ - (Opcional) A service simulada precisa retornar a exception not found. Utilizar o
        doThrow de modo similar ao teste realizado na service.
      */

     @Test
     public void testarDeleteClientRetornaNoContent204 () throws Exception {
        long idExistente = 10L;
            mockMvc.perform(delete("/clients/{id}", idExistente)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
     }

     @Test
     public void testarDeleteClientRetornaNoContent404 () throws Exception {
        long idNaoExistente = 100L;
            mockMvc.perform(get("/clients/{id}", idNaoExistente)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
     }

     /*
      * findByIncome deveria
        ◦ retornar OK (código 200), bem como os clientes que tenham o Income informado.
        Verificar se o Json Paginado tem a quantidade de clientes correta e se os clientes
        retornados são aqueles esperados. (similar ao exemplo feito em sala de aula).
        ◦ Cuidado com os valores para teste, pois o delete apagou algum registro:
        ◦ Código para passar o parâmetro income:
        mockMvc.perform(get("/clients/income/")

        .param("income", String.valueOf(salarioResultado))
        .accept(MediaType.APPLICATION_JSON));
      */
     @Test
     public void testarFindByIncomeClientRetornaOK200 () throws Exception {
        double income = 2500;
        int qtdClient = 3;
        mockMvc.perform(get("/clients/income/")
            .param("income", String.valueOf(income))
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content").exists())
            .andExpect(jsonPath("$.content").isArray())
            .andExpect(jsonPath("$.content[?(@.id == '%s')]", 2L).exists())
            .andExpect(jsonPath("$.content[?(@.id == '%s')]", 5L).exists())
            .andExpect(jsonPath("$.content[?(@.id == '%s')]", 12L).exists())
            .andExpect(jsonPath("$.content[?(@.name == '%s')]", "Lázaro Ramos").exists())
            .andExpect(jsonPath("$.content[?(@.name == '%s')]", "Gilberto Gil").exists())
            .andExpect(jsonPath("$.content[?(@.name == '%s')]", "Jorge Amado").exists())
            .andExpect(jsonPath("$.totalElements").value(qtdClient));
     }

     /*
      * update deveria
        ◦ retornar “ok” (código 200), bem como o json do produto atualizado para um id existente,
        verifique no mínimo dois atributos. (similar ao insert, precisa passar o json modificado).
        ◦ retornar “not found” (código 204) quando o id não existir. Fazer uma assertion para
        verificar no json de retorno se o campo “error” contém a string “Resource not found”.
      */
      @Test
      public void testarUpdateClientExistenteRetornaOK200 () throws Exception { 
        long idExistente = 11L;
        Integer children = 3;

        //('Silvio Almeida', '10164334861', 4500.0, TIMESTAMP WITH TIME ZONE '1970-09-23T07:00:00Z', 2)
        ClientDTO clienteUpdate = new ClientDTO(11L,"Silvio Almeida", "10164334861", 5500.0, Instant.parse("1970-09-23T07:00:00Z"), (Integer)3);
        String json = objectMapper.writeValueAsString(clienteUpdate);
            mockMvc.perform(put("/clients/{id}", idExistente)
                .content(json)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.income").exists())		
                .andExpect(jsonPath("$.income").value(5500.0))
                .andExpect(jsonPath("$.income").exists())		
                .andExpect(jsonPath("$.income").value(5500.0))
                .andExpect(jsonPath("$.children").exists())
                .andExpect(jsonPath("$.children").value(children));
      }

      @Test
      public void testarUpdateClientInexistenteRetornaNotFound204 () throws Exception { 
        long idInexistente = 1000L;
        //('Silvio Almeida', '10164334861', 4500.0, TIMESTAMP WITH TIME ZONE '1970-09-23T07:00:00Z', 2)
        ClientDTO clienteUpdate = new ClientDTO(1000L,"Cliente que não existe", "10164334861", 5500.0, Instant.parse("1970-09-23T07:00:00Z"), (Integer)3);
        String json = objectMapper.writeValueAsString(clienteUpdate);
            mockMvc.perform(put("/clients/{id}", idInexistente)
                .content(json)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.error").value("Resource not found"));
      }
}
