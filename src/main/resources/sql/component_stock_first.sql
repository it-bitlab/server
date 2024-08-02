select *,
       (select count(*) from component_stock_codes where code like csc.code || '%' and length(code) = length(csc.code) + 3 and removed = 0) as child_count
from component_stock_codes csc where id = &id

