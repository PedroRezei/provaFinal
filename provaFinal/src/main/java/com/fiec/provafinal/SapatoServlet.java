package com.fiec.provafinal;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.*;
import java.sql.*;
import java.util.*;

@WebServlet("/sapatos")
public class SapatoServlet extends HttpServlet {

    private Connection connection;

    @Override
    public void init() throws ServletException {
        super.init();

        // URL de conexão do MySQL
        String url = "jdbc:mysql://alunofiec.c6hic1eh5cuk.us-east-1.rds.amazonaws.com:3306/suabase";
        String username = "seu_usuario";  // Substitua com o nome de usuário real
        String password = "sua_senha";    // Substitua com a senha real

        try {
            // Carregar o driver JDBC do MySQL
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Tentar a conexão
            connection = DriverManager.getConnection(url, username, password);
            System.out.println("Conexão com o banco de dados estabelecida com sucesso!");
        } catch (ClassNotFoundException e) {
            throw new ServletException("Driver JDBC não encontrado.", e);
        } catch (SQLException e) {
            throw new ServletException("Erro ao conectar ao banco de dados.", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String modelo = request.getParameter("modelo");
        String cor = request.getParameter("cor");
        double preco = Double.parseDouble(request.getParameter("preco"));
        int tamanho = Integer.parseInt(request.getParameter("tamanho"));

        String sql = "INSERT INTO sapatos (modelo, cor, preco, tamanho) VALUES (?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, modelo);
            stmt.setString(2, cor);
            stmt.setDouble(3, preco);
            stmt.setInt(4, tamanho);
            stmt.executeUpdate();

            response.setStatus(HttpServletResponse.SC_CREATED);
            response.getWriter().write("Sapato inserido com sucesso.");
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Erro ao inserir sapato: " + e.getMessage());
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String sql = "SELECT * FROM sapatos";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            List<String> sapatos = new ArrayList<>();
            while (rs.next()) {
                String sapato = "ID: " + rs.getInt("id") + ", Modelo: " + rs.getString("modelo")
                        + ", Cor: " + rs.getString("cor") + ", Preço: " + rs.getDouble("preco")
                        + ", Tamanho: " + rs.getInt("tamanho");
                sapatos.add(sapato);
            }

            response.setContentType("application/json");
            PrintWriter out = response.getWriter();
            out.write("[");
            for (int i = 0; i < sapatos.size(); i++) {
                out.write("\"" + sapatos.get(i) + "\"");
                if (i < sapatos.size() - 1) {
                    out.write(",");
                }
            }
            out.write("]");

            response.setStatus(HttpServletResponse.SC_OK);
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Erro ao buscar sapatos: " + e.getMessage());
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int id = Integer.parseInt(request.getPathInfo().substring(1)); // Pegando o ID na URL
        String modelo = request.getParameter("modelo");
        String cor = request.getParameter("cor");
        double preco = Double.parseDouble(request.getParameter("preco"));
        int tamanho = Integer.parseInt(request.getParameter("tamanho"));

        String sql = "UPDATE sapatos SET modelo = ?, cor = ?, preco = ?, tamanho = ? WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, modelo);
            stmt.setString(2, cor);
            stmt.setDouble(3, preco);
            stmt.setInt(4, tamanho);
            stmt.setInt(5, id);

            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated > 0) {
                response.getWriter().write("Sapato atualizado com sucesso.");
                response.setStatus(HttpServletResponse.SC_OK);
            } else {
                response.getWriter().write("Sapato não encontrado.");
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Erro ao atualizar sapato: " + e.getMessage());
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int id = Integer.parseInt(request.getPathInfo().substring(1)); // Pegando o ID na URL

        String sql = "DELETE FROM sapatos WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            int rowsDeleted = stmt.executeUpdate();

            if (rowsDeleted > 0) {
                response.getWriter().write("Sapato deletado com sucesso.");
                response.setStatus(HttpServletResponse.SC_OK);
            } else {
                response.getWriter().write("Sapato não encontrado.");
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Erro ao deletar sapato: " + e.getMessage());
        }
    }

    // Fechando a conexão ao final
    @Override
    public void destroy() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            // Ignorar falha ao fechar a conexão
        }
    }
}
