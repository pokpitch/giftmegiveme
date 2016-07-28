import java.util.*;
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.*;
import java.lang.*;

public class SearchOrderHistory extends HttpServlet {

  private final DatabaseConnection openConnection = new DatabaseConnection();

	public void doGet(HttpServletRequest request, HttpServletResponse response)
		throws IOException, ServletException {

		Connection connect = null;
		PreparedStatement statement = null;
		HttpSession session = request.getSession();
		Member member = (Member)session.getAttribute("member");
		ArrayList<OrderHistory> list = new ArrayList <OrderHistory>();

		try {
			connect = openConnection.getDatabaseConnection();
			statement = connect.prepareStatement(
				"SELECT *,DATE_FORMAT(buy_date,'%d/%l/%Y %H:%m:%s')as'date' FROM travel t,member m,buy b WHERE m.member_id=t.member_id AND t.travel_id=b.travel_id AND b.member_id=? ORDER BY date DESC");
			statement.setString(1,member.getIdCard());
			ResultSet result = statement.executeQuery();

			while(result.next()){
				OrderHistory orderhistorys = new OrderHistory();
				orderhistorys.setProductName(result.getString("buy_name"));
				orderhistorys.setPicProduct(result.getString("buy_picture"));
				orderhistorys.setPriceProduct(result.getDouble("buy_price"));
				orderhistorys.setPiece(result.getInt("buy_amount"));
				orderhistorys.setCurrentDate(result.getString("date"));
				orderhistorys.setNameMember(result.getString("member_firstname"));
        orderhistorys.setLastNameMember(result.getString("member_lastname"));
				orderhistorys.setIdCard(result.getString("member_id"));
				list.add(orderhistorys);
			}
			session.setAttribute("order_history",list);
			result.close();
			statement.close();
			connect.close();

			RequestDispatcher rd = request.getRequestDispatcher("orderhistory.jsp");
			rd.forward(request, response);
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void doPost(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException {
			doGet(request, response);
	}
}
