{ Key generation routines used by mmbase }

{ drop function returnkey(integer);                             }
{ drop function returnkey(integer,integer);             }
{ drop function fetchrelkey();                                  }
{ drop function fetchrelkey(integer);                   }
{ drop table numbers;                                                   }


create table numbers(
        tabnum integer primary key,
        keynum integer
);

insert into numbers values (1,0);
insert into numbers values (2,0);


create function returnkey (tabkey integer) returns integer

        define  rtn     integer;

        let rtn = 1+ ((select keynum from numbers where tabnum = tabkey ));
        update numbers set keynum = rtn where tabnum = tabkey;

        return rtn;

end function;

create function fetchrelkey() returns integer
        define rtn integer;
        let rtn=returnkey(1);
        return rtn;
end function;

create function returnkey (tabkey integer,pl integer) returns integer

        define  rtn     integer;
        let rtn = 1+ ((select keynum from numbers where tabnum = tabkey ));
        update numbers set keynum = (rtn+pl-1) where tabnum = tabkey;

        return rtn;

end function;

create function fetchrelkey(pl integer) returns integer
        define rtn integer;
        let rtn=returnkey(1,pl);
        return rtn;
end function;

