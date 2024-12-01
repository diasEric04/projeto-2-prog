import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class App extends JFrame {
    private DefaultTableModel modeloTabela;

    public App() {
        setTitle("Bom Carro");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Configuração da tabela
        String[] colunas = {"ID", "Modelo", "Ano", "Quilometragem", "Valor", "Marca"};
        modeloTabela = new DefaultTableModel(colunas, 0);
        JTable tabela = new JTable(modeloTabela);
        JScrollPane scrollPane = new JScrollPane(tabela);

        // Carregar dados do banco de dados
        carregarDadosCarros();

        // Botões
        JButton btnEditar = new JButton("Editar Carro Selecionado");
        btnEditar.addActionListener(e -> editarCarro(tabela));

        JButton btnInserirVeiculo = new JButton("Inserir Veículo");
        btnInserirVeiculo.addActionListener(e -> new InserirVeiculos().setVisible(true)); // Abre a tela de inserir veículos

        JButton btnInserirMarca = new JButton("Inserir Marca");
        btnInserirMarca.addActionListener(e -> new InserirMarcas().setVisible(true)); // Abre a tela de inserir marcas

        JButton btnAtualizar = new JButton("Atualizar Lista");
        btnAtualizar.addActionListener(e -> carregarDadosCarros()); // Atualiza a lista de carros

        // Painel de botões
        JPanel panel = new JPanel();
        panel.add(btnEditar);
        panel.add(btnInserirVeiculo);
        panel.add(btnInserirMarca);
        panel.add(btnAtualizar); // Adiciona o botão de atualizar

        add(scrollPane, BorderLayout.CENTER);
        add(panel, BorderLayout.SOUTH);
    }

    public void carregarDadosCarros() {
        modeloTabela.setRowCount(0); // Limpa os dados da tabela

        try (Connection conn = DatabaseConnection.connect()) {
            String sql = "SELECT v.id, v.modelo, v.ano_fabricacao, v.quilometragem, v.valor, m.nome AS marca FROM veiculos v JOIN marcas m ON v.marca_id = m.id";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                Object[] linha = {
                    rs.getInt("id"),
                    rs.getString("modelo"),
                    rs.getInt("ano_fabricacao"),
                    rs.getInt("quilometragem"),
                    rs.getDouble("valor"),
                    rs.getString("marca")
                };
                modeloTabela.addRow(linha);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar dados: " + e.getMessage());
        }
    }

    private void editarCarro(JTable tabela) {
        int linhaSelecionada = tabela.getSelectedRow();
        if (linhaSelecionada == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um carro para editar.");
            return;
        }

        int id = (int) modeloTabela.getValueAt(linhaSelecionada, 0);
        String modelo = (String) modeloTabela.getValueAt(linhaSelecionada, 1);
        int ano = (int) modeloTabela.getValueAt(linhaSelecionada, 2);
        int quilometragem = (int) modeloTabela.getValueAt(linhaSelecionada, 3);
        double valor = (double) modeloTabela.getValueAt(linhaSelecionada, 4);

        String novoModelo = JOptionPane.showInputDialog(this, "Modelo:", modelo);
        String novoAno = JOptionPane.showInputDialog(this, "Ano:", ano);
        String novaQuilometragem = JOptionPane.showInputDialog(this, "Quilometragem:", quilometragem);
        String novoValor = JOptionPane.showInputDialog(this, "Valor:", valor);

        try (Connection conn = DatabaseConnection.connect()) {
            String sql = "UPDATE veiculos SET modelo = ?, ano_fabricacao = ?, quilometragem = ?, valor = ? WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, novoModelo);
            stmt.setInt(2, Integer.parseInt(novoAno));
            stmt.setInt(3, Integer.parseInt(novaQuilometragem));
            stmt.setDouble(4, Double.parseDouble(novoValor));
            stmt.setInt(5, id);

            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Carro atualizado com sucesso!");
            carregarDadosCarros(); // Atualiza a tabela
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao atualizar o carro: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new App().setVisible(true));
    }
}