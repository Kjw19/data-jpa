package study.datajpa.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;

import javax.persistence.LockModeType;
import javax.persistence.QueryHint;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom {

    List<Member> findByUsernameAndAgeGreaterThan(String username, int age);

    @Query(name = "Member.findByUsername")
    List<Member> findByUsername(@Param("username") String username);

    @Query("select m.username from Member m")
    List<String> findUsernameList();


    //@Query(name = "Member.findByUsername")로 주석해도돌아감
    //findByUsername실행 시 네임드쿼리 먼저 찾기때문
    //만약 네임드 쿼리 없을 시 메서드이름으로 쿼리 생성


    @Query("select m from Member m where m.username = :username and m.age = :age")
    List<Member> findUser(@Param("username") String username, @Param("age") int age);


    @Query("select new study.datajpa.dto.MemberDto(m.id, m.username, t.name) from Member m join m.team t")
    List<MemberDto> findMemberDto();

    @Query("select m from Member m where m.username in :names")
    List<Member> findByNames(@Param("names") Collection<String> names);

    List<Member> findListByUsername(String username); //컬렉션

    Member findMemberByUsername(String username);//단건

    Optional<Member> findOptionalByUsername(String username); //단건 Optional


    @Query(value = "select m from Member m left join m.team t",
            countQuery = "select count(m) from Member m")
        //간단할 때는 괜찮지만 복잡해지면 countQuery로 분리해서 성능 최적화!
    Page<Member> findByAge(int age, Pageable pageable);

    @Modifying(clearAutomatically = true) //쿼리가 나가고 난 후 자동으로 클리어 해줌
    @Query("update Member m set m.age = m.age + 1 where m.age >= :age")
    int bulkAgePlus(@Param("age") int age);

    @EntityGraph(attributePaths = {"team"})
    @Query("select m from Member m")
    List<Member> findMemberFetchJoin();


    @Override
    @EntityGraph(attributePaths = {"team"})
    List<Member> findAll();


   @EntityGraph(attributePaths = {"team"})
//    @EntityGraph("Member.all")
    List<Member> findEntityGraphByUsername(@Param("username") String username);

   @QueryHints(value = @QueryHint(name = "org.hibernate.readOnly", value = "true")) //이걸로 얻는 이점은 크지 않음
   //대부분 성능최적화 미스는 쿼리문 설계에서 문제, 없어도 성능 잘나옴
    Member findReadOnlyByUsername(String username);

   @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Member> findLockByUsername(String username);

}
