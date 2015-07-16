/*
    Author: Joshua Catral
    Date: 16/07/2015
    Program: A simple app that experiments with a basic pathfinder.
        Uses a linked list with four nodes to navigate the map in search for
        the end point. Efficiency on Ready to Program appears to vary, as this
        program is inefficient and will take up more time the more it has to
        search for the end.
*/

import java.applet.*;
import java.awt.*;
import java.io.*;
import java.util.*;
import java.awt.event.*;
import java.lang.Math;

public class MazeNavigator extends Applet implements Runnable, ActionListener
{
    //Variables
    Image img_wall, img_start, img_end, img_path;
    Label lbl_title, lbl_path, lbl_directions;
    Button btn_reset, btn_search;
    //These ints can affect the board, length & width referring to the size of
    //the array, and tile_size affecting how large each tile in the grid will be
    int length = 10, width = 10, tile_size = 50;
    int sx, sy, ex, ey;
    int state = 2;

    int map[] [] = new int [length] [width];
    NavigatorList Navi;
    // Initial startup for the program
    public void init ()
    {
        setLayout (null);
        setBackground (Color.white);
        img_wall = getImage (getCodeBase (), "WALL.PNG");
        img_start = getImage (getCodeBase (), "START.PNG");
        img_end = getImage (getCodeBase (), "END.PNG");
        img_path = getImage (getCodeBase (), "PATH.PNG");

        // Adding labels
        lbl_title = new Label ("Create a maze and the computer will find its way");
        add (lbl_title);
        lbl_title.setBounds (30, (width * tile_size + 10), 300, 25);

        lbl_path = new Label ("");
        add (lbl_path);
        lbl_path.setBounds (25, (width * tile_size + 30), 300, 25);

        lbl_directions = new Label ("");
        add (lbl_directions);
        lbl_directions.setBounds (5, (width * tile_size + 50), 300, 25);

        // Adding buttons
        btn_reset = new Button ("Reset");
        add (btn_reset);
        btn_reset.setBounds (340, (width * tile_size + 10), 50, 20);
        btn_reset.setActionCommand ("reset");
        btn_reset.addActionListener (this);

        btn_search = new Button ("Find a Way");
        add (btn_search);
        btn_search.setBounds (400, (width * tile_size + 10), 100, 20);
        btn_search.setActionCommand ("search");
        btn_search.addActionListener (this);
        // Initial map is an open field
        for (int x = 0 ; x < length ; x++)
        {
            for (int y = 0 ; y < width ; y++)
            {
                if (((x % (length - 1)) == 0) || ((y % (width - 1)) == 0))
                {
                    map [x] [y] = 1;
                }
                else
                {
                    map [x] [y] = 0;
                }
            }
        }

        // Randomly set start/end coordinates (no overlapping)
        sx = (int) (((length - 2) * Math.random ()) + 1);
        sy = (int) (((length - 2) * Math.random ()) + 1);
        do
        {
            ex = (int) (((length - 2) * Math.random ()) + 1);
            ey = (int) (((length - 2) * Math.random ()) + 1);
        }
        while (sx == ex && sy == ey);
        map [sx] [sy] = 2;
        map [ex] [ey] = 3;

        /*    Note that a single comment line can change what is commented out
        //Usingdatafileforinitial array map:
        try
        {
        BufferedReader filein;
        filein = new BufferedReader (new FileReader ("MapData.txt"));
        for (int i = 0 ; i < length ; i++)
        {
            String inputLine = filein.readLine ();
            StringTokenizer st = new StringTokenizer (inputLine, " ");
            for (int j = 0 ; j < width ; j++)
            {
                String eachNumber = st.nextToken ();
                int value = Integer.parseInt (eachNumber);
                    map [j] [i] = value;
                if (value == 2)
                {
                    sx = j;
                    sy = i;
                }
                else if (value == 3)
                {
                    ex = j;
                    ey = i;
                }
            }
        }
        filein.close ();
        }
        catch(IOExceptioncatche)
        {
        }
        //*/
        // Setting up navigation
        Navi = new NavigatorList ();
        Navi.SetStartPoint (sx, sy);

        map = Navi.PathFind (map);

    }


    public void run ()
    {

    }


    public boolean mouseDown (Event evt, int x, int y)
    {
        // State machine to proceed with setting down the start, end, and walls
        int mx = x / tile_size;
        int my = y / tile_size;
        if (x < (length * tile_size) && y < (width * tile_size) && x >= 0 && y >= 0)
        {
            switch (state)
            {
                case 0:
                    if (mx > 0 && mx < (length - 1) && my > 0 && my < (width - 1))
                    {
                        map [mx] [my] = 2;
                        sx = mx;
                        sy = my;
                        lbl_directions.setText ("Set an end point for the pathfinder.");
                        state = 1;
                    }
                    break;
                case 1:
                    if (mx > 0 && mx < (length - 1) && my > 0 && my < (width - 1) && map [mx] [my] != 2)
                    {
                        map [mx] [my] = 3;
                        ex = mx;
                        ey = my;
                        lbl_directions.setText ("Set as many walls as you want.");
                        state = 2;
                    }
                    break;
                case 2:
                    if (map [mx] [my] != 2 && map [mx] [my] != 3)
                        map [mx] [my] = 1;
                    break;
            }
            repaint ();
        }
        return true;
    }


    public void actionPerformed (ActionEvent e)
    {
        // Code for processing button input
        String order = e.getActionCommand ();

        if (order == "reset")
        {
            Reset (true);
        }
        if (order == "search" && state == 2)
        {
            Navi = new NavigatorList ();
            Navi.SetStartPoint (sx, sy);
            Reset (false);
            map = Navi.PathFind (map);
        }
        repaint ();
    }


    public void paint (Graphics g)
    {
        for (int x = 0 ; x < length ; x++)
        {
            for (int y = 0 ; y < width ; y++)
            {
                if (map [x] [y] == 1)
                {
                    g.drawImage (img_wall, (x * tile_size), (y * tile_size), tile_size, tile_size, this);
                }
                if (map [x] [y] == 2)
                {
                    g.drawImage (img_start, (x * tile_size), (y * tile_size), tile_size, tile_size, this);
                }
                if (map [x] [y] == 3)
                {
                    g.drawImage (img_end, (x * tile_size), (y * tile_size), tile_size, tile_size, this);
                }
                if (map [x] [y] == 4)
                {
                    g.drawImage (img_path, (x * tile_size), (y * tile_size), tile_size, tile_size, this);
                }
            }
        }
    }


    // Coordinate class to hold data about a given point in the 2-D array
    class Coordinate
    {
        int x, y, step;

        public Coordinate (int x, int y, int step)
        {
            this.x = x;
            this.y = y;
            this.step = step;
        }
    }


    // Linked list dedicated to the coordinate plane
    public class NavigatorList
    {
        // Node class for the navigator (which may be called upon)
        class Node
        {
            Node parent;
            Node link[] = new Node [4]; // 0 is right, 1 is up, 2 is left, 3 is down
            Coordinate place;
            boolean check;

            public Node (Coordinate location)
            {
                for (int a = 0 ; a < 4 ; a++)
                {
                    link [a] = null;
                }
                this.place = location;
                this.check = false;
                this.parent = null;
            }

        }

        Node start, end;
        // Linked list for holding data
        class List
        {
            class ListNode
            {
                ListNode link;
                Node data;

                public ListNode (Node data)
                {
                    link = null;
                    this.data = data;
                }
            }

            ListNode head;

            public List ()
            {
                head = null;
            }

            public void Add (Node mark)
            {
                ListNode fresh = new ListNode (mark);
                if (head == null)
                {
                    head = fresh;
                }
                else
                {
                    boolean check = false;
                    ListNode pos = head;
                    while (pos.link != null)
                    {
                        pos = pos.link;
                    }
                    pos.link = fresh;
                }
            }
            // Since we don't want to revisit a node, we can remove it from the list
            public void HeadRemove ()
            {
                head = head.link;
            }
        }

        public NavigatorList ()
        {
            start = null;
            end = null;
        }

        public void SetStartPoint (int x, int y)
        {
            Coordinate tmp = new Coordinate (x, y, 0);
            Node fresh = new Node (tmp);
            start = fresh;
        }

        public void Add (Node base, int dir, int x, int y, int step)
        {
            if (base != null)
            {
                Coordinate tmp = new Coordinate (x, y, step);
                Node fresh = new Node (tmp);
                fresh.parent = base;
                base.link [dir] = fresh;
            }
        }

        // Use a linked list to create a node tree that creates a node at every coordinate adjacent
        // Another linked list is used to determine which node is being checked.
        public int[] [] PathFind (int map[] [])
        {
            int[] [] world = map;
            int map_l = world [0].length;
            int map_w = world [1].length;

            List nodes = new List ();
            boolean TravelPath[] [] = new boolean [map_l] [map_w];
            boolean finish = false;
            for (int x = 0 ; x < map_l ; x++)
            {
                for (int y = 0 ; y < map_w ; y++)
                {
                    TravelPath [x] [y] = (world [x] [y] == 1);
                }
            }
            // Start with head and branch out
            nodes.Add (Navi.start);

            int base_x, base_y, look_x, look_y;
            double angle;
            Node pnt;
            do
            {
                pnt = nodes.head.data;
                TravelPath [pnt.place.x] [pnt.place.y] = true;
                if (!pnt.check && !finish)
                {
                    for (int d = 0 ; d < 4 ; d++)
                    {
                        angle = d * (Math.PI / 2);
                        base_x = pnt.place.x;
                        base_y = pnt.place.y;
                        look_x = pnt.place.x + Math.round ((float) Math.cos (angle));
                        look_y = pnt.place.y + Math.round ((float) Math.sin (angle));
                        if (!TravelPath [look_x] [look_y])
                        {
                            // Given a point adjacent to the place is clear
                            Navi.Add (pnt, d, look_x, look_y, pnt.place.step + 1);
                            nodes.Add (pnt.link [d]);
                            if (world [look_x] [look_y] == 3)
                            {
                                Navi.end = pnt.link [d];
                                finish = true;
                            }
                            /*else
                            {
                                world [look_x] [look_y] = 4;
                            }*/
                        }
                    }
                    pnt.check = true;
                    nodes.HeadRemove ();
                }
            }
            while (!finish && nodes.head != null);
            // If a path was found, trace it back
            if (finish)
            {
                String msg = "Path found, " + Integer.toString (Navi.end.place.step) + " steps taken.";
                lbl_path.setText (msg);
                pnt = Navi.end;
                do
                {
                    if (world [pnt.place.x] [pnt.place.y] == 0)
                    {
                        world [pnt.place.x] [pnt.place.y] = 4;
                    }
                    pnt = pnt.parent;
                }
                while (pnt.parent != null);
            }
            else
            {
                lbl_path.setText ("No Path Avaliable");
            }
            return world;
        }
    }


    //Function to clear out the grid
    public void Reset (boolean all)
    {
        for (int i = 0 ; i < width ; i++)
        {
            for (int j = 0 ; j < length ; j++)
            {
                // option to clear everything or just the path from the grid
                if (all && i % (width - 1) != 0 && j % (length - 1) != 0)
                {
                    map [j] [i] = 0;
                }

                else if (!all && map [j] [i] == 4)
                {
                    map [j] [i] = 0;
                }
            }
        }


        if (all)
        {
            state = 0;
            lbl_directions.setText ("Set a start point for the pathfinder.");
        }
    }
}
