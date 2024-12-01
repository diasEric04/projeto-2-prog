import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class InserirMarcas extends JFrame {
    private DefaultTableModel modeloTabela;

    public InserirMarcas() {
        setTitle("Gerenciamento de Marcas");
        setSize(600, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Configuração da tabela
        String[] colunas = {"ID", "Nome", "País de Origem"};
        modeloTabela = new DefaultTableModel(colunas, 0);
        JTable tabela = new JTable(modeloTabela);
        JScrollPane scrollPane = new JScrollPane(tabela);

        // Carregar dados do banco de dados
        carregarDadosMarcas();

        // Botão para editar a marca selecionada
        JButton btnEditar = new JButton("Editar Marca Selecionada");
        btnEditar.addActionListener(e -> editarMarca(tabela));

        // Adicionar componentes
        JPanel panel = new JPanel();
        panel.add(btnEditar);

        add(scrollPane, BorderLayout.CENTER);
        add(panel, BorderLayout.SOUTH);
    }

    private void carregarDadosMarcas() {
        modeloTabela.setRowCount(0); // Limpa os dados da tabela

        try (Connection conn = DatabaseConnection.connect()) {
            String sql = "SELECT * FROM marcas";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                Object[] linha = {
                    rs.getInt("id"),
                    rs.getString("nome"),
                    rs.getString("pais_origem")
                };
                modeloTabela.addRow(linha);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar marcas: " + e.getMessage());
        }
    }

    private void editarMarca(JTable tabela) {
        int linhaSelecionada = tabela.getSelectedRow();
        if (linhaSelecionada == -1) {
            JOptionPane.showMessageDialog(this, "Selecione uma marca para editar.");
            return;
        }

        int id = (int) modeloTabela.getValueAt(linhaSelecionada, 0);
        String nome = (String) modeloTabela.getValueAt(linhaSelecionada, 1);
        String paisOrigem = (String) modeloTabela.getValueAt(linhaSelecionada, 2);

        String novoNome = JOptionPane.showInputDialog(this, "Nome:", nome);
        String novoPais = JOptionPane.showInputDialog(this, "País de Origem:", paisOrigem);

        try (Connection conn = DatabaseConnection.connect()) {
            String sql = "UPDATE marcas SET nome = ?, pais_origem = ? WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, novoNome);
            stmt.setString(2, novoPais);
            stmt.setInt(3, id);

            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Marca atualizada com sucesso!");
            carregarDadosMarcas(); // Atualiza a tabela
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao atualizar a marca: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new InserirMarcas().setVisible(true));
    }
}