package com.tw.api.unit.test.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tw.api.unit.test.domain.todo.Todo;
import com.tw.api.unit.test.domain.todo.TodoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by yeli on 2019/7/2.
 */

@ExtendWith(SpringExtension.class)
@WebMvcTest(TodoController.class)
@ActiveProfiles(profiles = "test")
class TodoControllerTest {
    @Autowired
    private TodoController todoController;

    @Autowired
    private MockMvc mvc;

    @MockBean
    private TodoRepository todoRepository;

    @Test
    void getAll() throws Exception {

        //given
        final Todo todo = new Todo("Remove unused imports", true);
        List<Todo> todos = Arrays.asList(todo);
        when(todoRepository.getAll()).thenReturn(todos);
        //when
        ResultActions result = mvc.perform(get("/todos"));
        //then
        result.andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().json("[{'title': 'Remove unused imports','completed': true}]"));
    }

    @Test
    void getTodoWhenIdIsFound() throws Exception {
        //given
        final Todo todo = new Todo("Remove unused imports", true);
        Optional<Todo> todoOptional = Optional.of(todo);
        when(todoRepository.findById(1L)).thenReturn(todoOptional);

        //when
        ResultActions result = mvc.perform(get("/todos/1"));

        //then
        result.andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().json("{'title': 'Remove unused imports','completed': true}"));
    }

    @Test
    void returnNotFoundWhenIdIsFound() throws Exception {
        //given
        Optional<Todo> todoOptional = Optional.empty();
        when(todoRepository.findById(1L)).thenReturn(todoOptional);

        //when
        ResultActions result = mvc.perform(get("/todos/1"));

        //then
        result.andExpect(status().isNotFound());
    }

    @Test
    void saveTodo() throws Exception {
        //given

        final Todo todo = new Todo("Remove unused imports", true);
        List<Todo> todos = Arrays.asList(todo);
        final String jsonContent = "{title: 'Remove unused imports','completed': true}";

        when(todoRepository.getAll()).thenReturn(todos);

        //when
        ResultActions result = mvc.perform(post("/todos")
                .content(asJsonString(todo))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        //then
        result.andExpect(status().isCreated())
                .andDo(print())
                .andExpect(content().json(jsonContent));
    }

    @Test
    void deleteOneTodoWhenIdIsFound() throws Exception {
        //given
        final Todo todo = new Todo("Remove unused imports", true);
        Optional<Todo> todoOptional = Optional.of(todo);
        when(todoRepository.findById(1L)).thenReturn(todoOptional);

        //when
        ResultActions result = mvc.perform(delete("/todos/1"));

        //then
        result.andExpect(status().isOk());
    }

    @Test
    void returnNotFoundForDeleteRequestWhenIdIsFound() throws Exception {
        //given
        Optional<Todo> todoOptional = Optional.empty();
        when(todoRepository.findById(1L)).thenReturn(todoOptional);

        //when
        ResultActions result = mvc.perform(get("/todos/1"));

        //then
        result.andExpect(status().isNotFound());
    }

    @Test
    void updateTodoWhenIdExists() throws Exception {
        //given

        final Todo todo = new Todo("Remove unused imports", true);
        final Todo newTodo = new Todo("Clean the code", true);
        Optional<Todo> todoOptional = Optional.of(todo);
        when(todoRepository.findById(1L)).thenReturn(todoOptional);
        final String jsonContent = "{title: 'Clean the code','completed': true}";

        //when
        ResultActions result = mvc.perform(patch("/todos/1")
                .content(asJsonString(newTodo))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        //then
        result.andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().json(jsonContent));
    }

    @Test
    void returnNotFoundForUpdateRequestWhenIdIsFound() throws Exception {
        //given

        final Todo todo = new Todo("Remove unused imports", true);
        final Todo newTodo = new Todo("Clean the code", true);
        Optional<Todo> todoOptional = Optional.empty();
        when(todoRepository.findById(1L)).thenReturn(todoOptional);

        //when
        ResultActions result = mvc.perform(patch("/todos/1")
                .content(asJsonString(newTodo))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        //then
        result.andExpect(status().isNotFound());
    }

    @Test
    void returnBadRequestForUpdateRequestWhenNewTodoIsNull() throws Exception {
        //given

        final Todo todo = new Todo("Remove unused imports", true);
        final Todo newTodo = new Todo("Clean the code", true);
        Optional<Todo> todoOptional = Optional.empty();
        when(todoRepository.findById(1L)).thenReturn(todoOptional);

        //when
        ResultActions result = mvc.perform(patch("/todos/1")
                .content("")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        //then
        result.andExpect(status().isBadRequest());
    }

    private static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}