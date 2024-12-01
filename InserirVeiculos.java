import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class InserirVeiculos extends JFrame {
    private JTextField txtModelo, txtAno, txtQuilometragem, txtValor;
    private JComboBox<String> comboMarcas;
    private JButton btnSalvar;

    public InserirVeiculos() {
        setTitle("Inserir Veículo");
        setLayout(new GridLayout(6, 2));

        // Campos e botões
        add(new JLabel("Modelo do Veículo:"));
        txtModelo = new JTextField();
        add(txtModelo);

        add(new JLabel("Ano de Fabricação:"));
        txtAno = new JTextField();
        add(txtAno);

        add(new JLabel("Quilometragem:"));
        txtQuilometragem = new JTextField();
        add(txtQuilometragem);

        add(new JLabel("Valor:"));
        txtValor = new JTextField();
        add(txtValor);

        add(new JLabel("Marca:"));
        comboMarcas = new JComboBox<>();
        carregarMarcas();
        add(comboMarcas);

        btnSalvar = new JButton("Salvar");
        add(btnSalvar);

        btnSalvar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                salvarVeiculo();
            }
        });

        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
    }

    private void carregarMarcas() {
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement stmt = conn.prepareStatement("SELECT id, nome FROM marcas");
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                comboMarcas.addItem(rs.getInt("id") + " - " + rs.getString("nome"));
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar marcas: " + ex.getMessage());
        }
    }

    private void salvarVeiculo() {
        String modelo = txtModelo.getText();
        int ano = Integer.parseInt(txtAno.getText());
        int quilometragem = Integer.parseInt(txtQuilometragem.getText());
        double valor = Double.parseDouble(txtValor.getText());
        String marca = (String) comboMarcas.getSelectedItem();
        int marcaId = Integer.parseInt(marca.split(" - ")[0]);

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO veiculos (modelo, ano_fabricacao, quilometragem, valor, marca_id) VALUES (?, ?, ?, ?, ?)")) {

            stmt.setString(1, modelo);
            stmt.setInt(2, ano);
            stmt.setInt(3, quilometragem);
            stmt.setDouble(4, valor);
            stmt.setInt(5, marcaId);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Veículo inserido com sucesso!");
            txtModelo.setText("");
            txtAno.setText("");
            txtQuilometragem.setText("");
            txtValor.setText("");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao salvar: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        new InserirVeiculos();
    }
}