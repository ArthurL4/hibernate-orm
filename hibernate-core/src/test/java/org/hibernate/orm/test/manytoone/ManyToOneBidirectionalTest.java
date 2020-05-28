/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html
 */
package org.hibernate.orm.test.manytoone;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import org.hibernate.testing.jdbc.SQLStatementInspector;
import org.hibernate.testing.orm.junit.DomainModel;
import org.hibernate.testing.orm.junit.SessionFactory;
import org.hibernate.testing.orm.junit.SessionFactoryScope;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hibernate.orm.test.manytoone.ManyToOneBidirectionalTest.*;

/**
 * @author Andrea Boriero
 */
@DomainModel(
		annotatedClasses = {
				EntityTest.class,
				EntityTest2.class
		}
)
@SessionFactory(statementInspectorClass = SQLStatementInspector.class)
public class ManyToOneBidirectionalTest {

	@BeforeEach
	public void setUp(SessionFactoryScope scope) {
		scope.inTransaction(
				session -> {
					EntityTest entity = new EntityTest( 1, "e1" );
					EntityTest2 entity2 = new EntityTest2( 2, "e2" );
					EntityTest entity3 = new EntityTest( 3, "e3" );
					entity.setEntity2( entity2 );
					entity2.setEntity( entity3 );
					session.save( entity3 );
					session.save( entity2 );
					session.save( entity );
				}
		);
	}

	@Test
	public void testGet(SessionFactoryScope scope) {
		SQLStatementInspector statementInspector = (SQLStatementInspector) scope.getStatementInspector();
		statementInspector.clear();
		scope.inTransaction(
				session -> {
					EntityTest entity = session.get( EntityTest.class, 1 );

					EntityTest2 entity2 = entity.getEntity2();
					assertThat( entity2.getName(), is( "e2" ) );

					EntityTest entity3 = entity2.getEntity();
					assertThat( entity3.getName(), is( "e3" ) );

					statementInspector.assertExecutedCount( 1 );
					statementInspector.assertNumberOfOccurrenceInQuery( 0, "join", 2 );
				}
		);
	}

	@Entity(name = "EntityTest")
	public static class EntityTest {
		@Id
		private Integer id;

		private String name;

		@ManyToOne
		private EntityTest2 entity2;

		public EntityTest() {
		}

		public EntityTest(int id, String name) {
			this.id = id;
			this.name = name;
		}

		public Integer getId() {
			return id;
		}

		public void setId(Integer id) {
			this.id = id;
		}

		public EntityTest2 getEntity2() {
			return entity2;
		}

		public void setEntity2(EntityTest2 entity2) {
			this.entity2 = entity2;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
	}

	@Entity(name = "EntityTest2")
	public static class EntityTest2 {
		@Id
		private Integer id;

		private String name;

		@ManyToOne
		private EntityTest entity;

		public EntityTest2() {
		}

		public EntityTest2(int id, String name) {
			this.id = id;
			this.name = name;
		}

		public Integer getId() {
			return id;
		}

		public void setId(Integer id) {
			this.id = id;
		}

		public EntityTest getEntity() {
			return entity;
		}

		public void setEntity(EntityTest entity) {
			this.entity = entity;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
	}
}
