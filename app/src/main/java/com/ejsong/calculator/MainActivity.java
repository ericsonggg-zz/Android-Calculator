package com.ejsong.calculator;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
//import android.view.ViewTreeObserver;
import android.widget.TextView;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

public class MainActivity extends AppCompatActivity {

    private TextView view;
    private String [] text;
    private int maxLines = 5;
    private int maxChars = 16;
    private int paranCount = 0;
    private String answer ="0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        view = (TextView) findViewById(R.id.textField);

       /*  ViewTreeObserver vtobs = view.getViewTreeObserver();
       // if (vtobs.isAlive())
        //{
            vtobs.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    Log.d ("dimensions", Integer.toString(view.getMaxLines()));
                    Log.d ("dimensions", Integer.toString(view.getWidth())+" " + Integer.toString(view.getMeasuredWidth()));
                    maxLines = view.getMaxLines();
                    maxChars = (int) view.getWidth() / view.getMeasuredWidth();
                    view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
         //   }
        }); */

        text = new String [maxLines];
        for (int x = 0 ; x < maxLines; x++)
            text [x] = "";
        text [maxLines-1] = "0";
        refreshTextView();
    }

    private void updateText (String s)
    {
        String editLine = text [maxLines-1];
        if (s.equals("Ans") && editLine.length() < maxChars - 3) {
            if (editLine.equals("0"))
                editLine = "Ans";
            else
                editLine = editLine.concat(s);
        }
        else
        {
            if (editLine.length() < maxChars)
                editLine = editLine.concat(s);
        }
        text [maxLines-1] = editLine;
        refreshTextView();
    }
    private void updateText (int i)
    {
        String editLine = text [maxLines-1];
        if (editLine.equals("0"))
            editLine = Integer.toString(i);
        else if (editLine.length() < maxChars)
            editLine = editLine.concat(Integer.toString(i));
        else
            editLine = editLine.substring(0,maxChars-1).concat(Integer.toString(i));
        text [maxLines-1] = editLine;
        refreshTextView();
    }

    private void refreshTextView ()
    {
        String t = "";
        for (int x = 0; x < text.length; x++)
        {
            t = t.concat (text[x]);
            if (x != maxLines-1)
                t = t.concat ("\n");
        }
        view.setText(t);
    }

    public void pressNumber (View view)
    {
        if (view.getId() == R.id.button0)
            updateText (0);
        else if (view.getId() == R.id.button1)
            updateText (1);
        else if (view.getId() == R.id.button2)
            updateText (2);
        else if (view.getId() == R.id.button3)
            updateText (3);
        else if (view.getId() == R.id.button4)
            updateText (4);
        else if (view.getId() == R.id.button5)
            updateText (5);
        else if (view.getId() == R.id.button6)
            updateText (6);
        else if (view.getId() == R.id.button7)
            updateText (7);
        else if (view.getId() == R.id.button8)
            updateText (8);
        else
            updateText (9);
    }

    public void pressSymbol (View view)
    {
        if (view.getId() == R.id.buttonPlus)
            updateText("+");
        else if (view.getId() == R.id.buttonMinus)
            updateText("-");
        else if (view.getId() == R.id.buttonMultiply)
            updateText("*");
        else if (view.getId() == R.id.buttonDivide)
            updateText("/");
        else if (view.getId() == R.id.buttonPeriod)
            updateText(".");
        else if (view.getId() == R.id.buttonAns)
            updateText("Ans");
        else
        {
            if (text [maxLines-1].length() == 0) //empty line
            {
                updateText("(");
                paranCount++;
                return;
            }
            int lastChar = text [maxLines-1].charAt (text[maxLines-1].length()-1);
            if (lastChar == ')'|| lastChar == 46 || lastChar >= 48 && lastChar <= 57) //. or number
            {
                if (paranCount > 0) {
                    updateText(")");
                    paranCount--;
                }
                else {
                    updateText("*(");
                    paranCount++;
                }
            }
            else //symbols
            {
                updateText ("(");
                paranCount++;
            }

        }
    }

    public void pressClear (View view)
    {
        text [maxLines-1]= "";
        refreshTextView();
    }

    public void pressBack (View view)
    {
        if (text [maxLines-1].length() > 0) {
            if (text[maxLines-1].charAt (text[maxLines-1].length()-1) == 's')
                text [maxLines-1] = text[maxLines-1].substring(0, text[maxLines-1].length()-3);
            else if (text[maxLines-1].length() == 1)
                text [maxLines-1] = "0";
            else {
                if (text[maxLines-1].charAt(text[maxLines-1].length()-1) == '(')
                    paranCount--;
                else
                    if (text[maxLines-1].charAt(text[maxLines-1].length()-1)==')')
                        paranCount++;
                text [maxLines-1] = text[maxLines-1].substring(0, text[maxLines-1].length() - 1);
            }
        }
        refreshTextView();
    }

    public void pressEquals (View view)
    {
        Queue <String> postfix = new LinkedList<String>();
        Stack <Character> operators = new Stack <Character> ();

        for (int x = 0; x < paranCount; x++) //add closing parans
            text [maxLines-1]+= ")";
        paranCount = 0;
        refreshTextView();

        //parse to postfix (Shunting-yard algorithm)
        char [] textLine = text [maxLines-1].toCharArray();
        int index = 0;
        boolean isNegative = false, skipNegative = false;
        while (index < textLine.length)
        {
            for (int x = index; x < textLine.length; x++)
            {
                if (textLine [x] != 46 && (textLine [x] < 48 || textLine [x] > 57)) //search for next operator
                {
                    if (x > 0 && (textLine [x-1] >= 48 && textLine [x-1] <= 57 || textLine [x-1] == '.')) //add number if previous was number
                    {
                        String temp = ""; //add number to queue
                        if (isNegative)
                            temp+="-";
                        for (int y = index; y < x; y++) {
                            temp += Character.toString(textLine[y]);
                        }
                        postfix.add(temp);
                        isNegative = false;
                    }
                    else
                    {
                        if (textLine[x] == '-' && (x == 0 || x > 0 && (textLine [x-1] == '+' || textLine [x-1] == '-' || textLine [x-1] == '*' || textLine [x-1] == '/' || textLine[x-1] == '(')))
                        {
                            isNegative = true;
                            skipNegative = true;
                        }
                    }
                    if (textLine [x] == 'A')
                    {
                        if (isNegative) {
                            if (answer.charAt(0) == '-')
                                postfix.add (answer.substring(1,answer.length()));
                            else
                                postfix.add("-" + answer);
                            isNegative = false;
                        }
                        else
                            postfix.add(answer);
                        x+=2;
                    }
                    else if (textLine [x] == ')') //if closing paran, pop all from stack until opening paran is found.
                    {
                        while (operators.size() != 0 && operators.peek() != '(')
                        {
                            if (operators.size() == 0)
                            {
                                //SYNTAX ERROR DIALOGUE
                                return;
                            }
                            postfix.add(Character.toString(operators.pop()));
                        }
                        if (operators.size() <= 0)
                            return;
                        operators.pop();
                    }
                    else if (textLine [x] == '+' || textLine [x] == '-') // if weak operator, pop strong operators first
                    {
                        if (textLine [x] == '-' && skipNegative) {
                            skipNegative = false;
                        }
                        else {
                            while (!operators.empty() && (operators.peek() == '*' || operators.peek() == '/')) {
                                postfix.add(Character.toString(operators.pop()));
                            }
                            operators.push(textLine[x]);
                        }
                    }
                    else
                    {
                        operators.push(textLine[x]);
                    }
                    index = x + 1;
                    break;
                }
                if (x >= textLine.length - 1) //last number
                {
                    String temp = ""; //add number to queue
                    if (isNegative) {
                        temp += "-";
                        isNegative = false;
                    }
                    for (int y = index; y <= x; y++)
                    {
                        temp+= Character.toString(textLine [y]);
                    }
                    postfix.add(temp);
                    index = textLine.length;
                }
            }
        }
        while (!operators.empty()) {
            char a = operators.pop();
            postfix.add(Character.toString(a));
        }

        //compute answer from postfix
        Stack <Double> math = new Stack <Double> ();
        while (!postfix.isEmpty())
        {
            if (!postfix.peek().equals("+") && !postfix.peek().equals("-") && !postfix.peek().equals("*") && !postfix.peek().equals("/")) //is a number
            {
                try {
                        math.push(Double.parseDouble(postfix.remove()));
;               }
                catch (NumberFormatException e)
                {
                    //Syntax error dialogue
                    return;
                }
                catch (Exception e)
                {
                    return;
                }
            }
            else
            {
                if (math.size() <= 1)
                {
                    //Syntax error dialogue
                    return;
                }
                else
                {
                    double second = math.pop();
                    double first = math.pop();
                    String operation = postfix.remove();
                    if (operation.equals ("+"))
                    {
                        math.push(first + second);
                    }
                    else if (operation.equals("-"))
                    {
                        math.push (first - second);
                    }
                    else if (operation.equals("*"))
                    {
                        math.push (first * second);
                    }
                    else
                    {
                        if (second == 0)
                            return;
                        math.push (first / second);
                    }
                }
            }
        }
        if (math.size() != 1)
        {
            //syntax error
            return;
        }
        double ans = math.pop();
        if (ans % 1 == 0)
            answer = Integer.toString((int) ans);
        else
            answer = Double.toString(ans);
        for (int x =0 ; x <maxLines-1; x++)
            text [x] = text [x+1];
        text [maxLines-1] = answer;
        refreshTextView();
    }
}
