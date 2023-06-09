package com.market.carrot.member.chat;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.market.carrot.dto.ChatConTentDTO;
import com.market.carrot.rowmapper.ChatContentRowMapper;

@Repository
public class ChatDAOImpl implements ChatDAO {

	@Autowired
	private JdbcTemplate mytemplate;

	@Override
	public List<ChatConTentDTO> messageList(ChatConTentDTO dto) {
		// TODO Auto-generated method stub
		String nick = dto.getNick();
		List<ChatConTentDTO> list = mytemplate.query(
				"select * from chat_content where user_id = ? OR other_nick = ? group by user_id order by send_at desc",
				new Object[] { nick, nick }, new ChatContentRowMapper());
		for (ChatConTentDTO data : list) {
			String sql = "SELECT DISTINCT i.user_id FROM items i JOIN chat c ON i.items_id = c.items_id JOIN chat_content cc ON c.chat_id = cc.chat_id WHERE cc.user_id = ?";
			try {
				String other_nick = mytemplate.queryForObject(sql, new Object[] { nick }, String.class);
				data.setOther_nick(other_nick);
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		System.out.println("messageList " + list);
		return list;
	}

	@Override
	public List<ChatConTentDTO> roomContentList(ChatConTentDTO dto) {
		// TODO Auto-generated method stub
		List<ChatConTentDTO> list = mytemplate.query("select * from chat_content where chat_id = ?",
				new Object[] { dto.getChat_id() }, new ChatContentRowMapper());
		for (ChatConTentDTO data : list) {
			String sql = "SELECT DISTINCT i.user_id FROM items i JOIN chat c ON i.items_id = c.items_id WHERE c.chat_id =?";
			try {
				String other_nick = mytemplate.queryForObject(sql, new Object[] { dto.getChat_id() }, String.class);
				data.setOther_nick(other_nick);
				System.out.println("roomContentList 조회완료");
			} catch (Exception e) {
				// TODO: handle exception
			}
		}

		return list;
	}

	@Override
	public int messageSendInlist(ChatConTentDTO dto) {
		// TODO Auto-generated method stub
		// SELECT i.items_id FROM items i JOIN chat c ON i.items_id = c.items_id WHERE
		// i.user_id = 'test' AND c.chat_id =2
		String sql = "SELECT i.items_id FROM items i JOIN chat c ON i.items_id = c.items_id WHERE i.user_id = ? AND c.chat_id = ?";
		//System.out.println(dto.getOther_nick() + "/" + dto.getChat_id());
		System.out.println("dto.getRecv " + dto.getOther_nick());
		System.out.println("dto.getChat " + dto.getChat_id());
		
		int items_id = 0;
		try {
			items_id = mytemplate.queryForObject(sql, new Object[] { dto.getOther_nick(), dto.getChat_id() },
					Integer.class);
			System.out.println("items_id " + items_id);
			sql = "insert into chat_content (user_id, chat_id, items_id, other_nick, content, send_at, type, use_at) values(?,?,?,?,?,sysdate(),?,'Y')";

			return mytemplate.update(sql, dto.getUser_id(), dto.getChat_id(), items_id,dto.getOther_nick(), dto.getContent(), "t");
		} catch (DataAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		}
	}

	@Override
	public int createChatRoom(int items_id) {
		// TODO Auto-generated method stub

		String sql = "INSERT INTO chat VALUES (null,?,sysdate())";

		int result = mytemplate.update(sql, items_id);

		return result;
	}

	@Override
	public int messageSend(String user_id, int items_id) {
		int chat_id = searchChatLastId();
		String recv_id= searchRecvId(items_id);
		int result = 0;
		if (chat_id > 0) {
			String sql = "insert into chat_content (user_id, chat_id, items_id, other_nick, content, send_at, type, use_at) values(?,?,?,?,'해당 제품 관심있어서 연락드립니다' ,sysdate(), 'T' ,'Y')";


			result = mytemplate.update(sql, user_id, chat_id, items_id, recv_id);
		}
		return result;
	}

	@Override
	public int searchChatLastId() {
		String sql = "select chat_id from chat order by chat_id DESC LIMIT 1";
		int result = 0;
		try {
			result = mytemplate.queryForObject(sql, Integer.class);
		} catch (DataAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
	
	@Override
	public int getCountChatFromItem(int items_id) {
		// TODO Auto-generated method stub
		String sql = "select count(*) from chat where items_id = ?";
		int result = 0;
		try {
			result = mytemplate.queryForObject(sql, Integer.class, items_id);
		} catch (DataAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
	
	public String searchRecvId(int items_id) {
		String sql = "select user_id from items where items_id = ?";
		String result = "";
		try {
			result = mytemplate.queryForObject(sql, String.class,items_id );
		} catch (DataAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

}
