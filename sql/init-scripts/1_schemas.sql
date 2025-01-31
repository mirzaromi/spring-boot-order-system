CREATE TYPE "notif_status" AS ENUM ('SUCCESS', 'FAILED', 'PENDING');
CREATE TYPE "order_status" AS ENUM (
    'SUCCESS', 'FAILED', 'PENDING', 'RESERVED'
    );
CREATE TYPE "payment_status" AS ENUM ('SUCCESS', 'PENDING', 'FAILED');
create table public.payment (
                                id bigint generated by default as identity primary key,
                                amount double precision not null,
                                created_at timestamp(6) not null,
                                status varchar(20) not null,
                                order_id bigint not null
);
alter table
    public.payment owner to postgres;
create table public.inventory (
                                  id bigint generated by default as identity primary key,
                                  product_id varchar(255),
                                  name varchar(255) not null,
                                  stock integer not null,
                                  available boolean not null,
                                  price double precision not null,
                                  created_at timestamp(6) not null,
                                  updated_at timestamp(6) not null
);
alter table
    public.inventory owner to postgres;
create table public.users (
                              id bigint generated by default as identity primary key,
                              name varchar(255) not null,
                              created_at timestamp(6) not null,
                              updated_at timestamp(6) not null
);
alter table
    public.users owner to postgres;
create table public.orders (
                                id bigint generated by default as identity primary key,
                                user_id bigint not null constraint fkoi4kr85e783q0o7m4yo3tykjl references public.users,
                                status varchar(20),
                                total_items integer not null,
                                total_price double precision not null,
                                remark varchar(255),
                                created_at timestamp(6) not null,
                                updated_at timestamp(6) not null

);
alter table
    public.orders owner to postgres;
create table public.notification (
                                      id bigint generated by default as identity primary key,
                                      user_id bigint not null constraint fknk812r22hkwm5qv9c5nf2wlgy references public.users,
                                      order_id bigint not null constraint fk8ec3mroggermua3g3dsm618ad references public.orders,
                                    --   event_type varchar(255) not null,
                                      message varchar(255) not null,
                                      status varchar(20) not null,
                                      notification_type varchar(20) not null,
                                      remark varchar(255),
                                      created_at timestamp(6) not null,
                                      updated_at timestamp(6) not null
);
alter table
    public.notification owner to postgres;
create table public.order_details (
                                      id bigint generated by default as identity primary key,
                                      inventory_id bigint not null constraint fkry1t8ls2p1a4jae8h32r5et74 references public.inventory,
                                      order_id bigint not null constraint fkjyu2qbqt8gnvno9oe9j2s2ldk references public.orders,
                                      price double precision not null,
                                      quantity integer not null,
                                      created_at timestamp(6) not null,
                                      updated_at timestamp(6) not null
);
alter table
    public.order_details owner to postgres;
create table public.payments (
                                  id bigint generated by default as identity primary key,
                                  order_id bigint not null constraint fk81gagumt0r8y3rmudcgpbk42l references public.orders,
                                  amount double precision not null,
                                  status varchar(255) not null,
                                  remark varchar(255),
                                  created_at timestamp(6) not null,
                                  updated_at timestamp(6) not null
);
alter table
    public.payments owner to postgres;
