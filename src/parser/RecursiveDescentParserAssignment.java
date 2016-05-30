package parser;

import java.util.Map;
import java.util.HashMap;

import static lexer.LexerGenerator.Token;
/**
 * Recursive descent parser for assignment of the form: int id = num;
 * 
 * Grammar:
 *  1: start      -> assignment SEMICOLON EOF
 *  2: assignment -> INT ID ASSIGN expr
 *  3: expr       -> ID subexpr
 *  4: expr       -> NUMBER subexpr
 *  5: expr       -> LPAR expr RPAR
 *  6: expr       -> READ LPAR RPAR subexpr
 *  7: subexpr    -> PLUS expr
 *  8: subexpr    -> MINUS expr
 *  9: subexpr    -> TIMES expr
 * 10: subexpr    -> DIV expr
 * 11: subexpr    -> MOD expr
 * 12: subexpr    -> eps
 */
public class RecursiveDescentParserAssignment extends RecursiveDescentParser {
	Map< Token, Integer > rulesNumberAssignments;
	
	public RecursiveDescentParserAssignment()
	{
		rulesNumberAssignments = new HashMap< Token, Integer >();
		rulesNumberAssignments.put( Token.PLUS, 7 );
		rulesNumberAssignments.put( Token.MINUS, 8 );
		rulesNumberAssignments.put( Token.TIMES, 9 );
		rulesNumberAssignments.put( Token.DIV, 10 );
		rulesNumberAssignments.put( Token.MOD, 11 );
	}
	/**
	 * Starting symbol of the grammar.
	 * 
	 * @throws ParserException
	 *             Exception from the parser.
	 */
	protected void main() throws ParserException
	{   
		next();
        start();
        
        if ( ! symbols.isEmpty() ) {
			printError("Symbols remaining.");
		}
	}
	
	/**
	 * 
	 *  fi( start ) = { INT }, fo( start ) = { eps }
	 */
	private void start() throws ParserException
	{
			print( 1 );
			assignment();
			
			ensureTokenEQ( Token.SEMICOLON );
			next();
			ensureTokenEQ( Token.EOF );
	}
	/**
	 * fi( assignment ) = { INT },fo( assignment ) = { SEMICOLON }
	 */
	private void assignment() throws ParserException
	{
		ensureTokenEQ( Token.INT );
		next();
		ensureTokenEQ( Token.ID );
		next();
		ensureTokenEQ( Token.ASSIGN );
		print( 2 );
		next();
		expr();
	}
	/**
	 * 
	 *  *  3: expr       -> ID subexpr
 *  4: expr       -> NUMBER subexpr
 *  5: expr       -> LPAR expr RPAR
 *  6: expr       -> READ LPAR RPAR subexpr
 *  
	 * fi( expr ) = { ID, NUMBER, LPAR, READ }, fo( expr ) = { SEMICOLON }
	 */
	private void expr() throws ParserException
	{
		switch ( token )
		{
			case ID:
				print( 3 );
				next();
				subExpr();
				break;
			case NUMBER:
				print( 4 );
				next();
				subExpr();
				break;
			case LPAR:
				print( 5 );
				next();
				expr();
				ensureTokenEQ( Token.RPAR );
				next();
				break;
			case READ:
				next();
				ensureTokenEQ( Token.LPAR );
				next();
				ensureTokenEQ( Token.RPAR );
				print( 6 );
				next();
				subExpr();
				break;
			case SEMICOLON:
				break;
			default:
				printError( "Read invalid token " + token );
		}			
	}
	/**
	 * 
	 *  *  7: subexpr    -> PLUS expr
 *  8: subexpr    -> MINUS expr
 *  9: subexpr    -> TIMES expr
 * 10: subexpr    -> DIV expr
 * 11: subexpr    -> MOD expr
 * 12: subexpr    -> eps
	 * fi( subexpr ) = { PLUS, MINUS, TIMES, DIV, MOD, EPS }, fo( subexpr ) = { SEMICOLON, RPAR }
	 */
	private void subExpr() throws ParserException
	{
		if ( token == Token.SEMICOLON || token == Token.RPAR ) // fo( subexpr )
		{
			print( 12 );
			return;
		}
		else if ( rulesNumberAssignments.get( token ) != null )
		{			
			print( rulesNumberAssignments.get( token ) );
			next();
			expr();
		}
		else
			printError( "Invalid Input in subExpr: " + token );
	}
	/**
	 * Helper method to avoid ugly if-blocks everywhere.
	 * 
	 * @param t expected value of next token
	 * @throws ParserException
	 */
	private void ensureTokenEQ( Token t ) throws ParserException
	{
		if ( token != t )
		{
			printError( "Expected: " + t + " Actual: " + token );
		}
	}
}
