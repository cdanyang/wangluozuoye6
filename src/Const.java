/**
 * This class defines constants used across Router and Client
 * 
 * @author Shu Zhao
 * 
 */
public class Const {
	// Header format
	// | dest | src | seq num | last packet|
	public static final int HEADER_SIZE = 4;
	// Actual data size in one packet
	public static final int BUFFER_SIZE = 1024;

	public static final String FILE_A = "fileA.txt";
	public static final String FILE_B = "fileB.txt";

	public static final int MAX_CLIENT = 10;

	public static final int CLIENT_MAX_WAIT_TIME = 5000;
}
